package kopo.poly.service;

import kopo.poly.dto.WasteGuide;
import kopo.poly.dto.WasteGuide.Company;
import kopo.poly.dto.WasteGuide.Section;
import kopo.poly.dto.WasteGuide.Table;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WasteCrawlerService {

    private static final String BASE           = "https://www.chuncheon.go.kr/cityhall/living-info/clean-environment/waste-discharge/";
    private static final String GENERAL        = BASE + "general/";
    private static final String RECYCLABLE     = BASE + "recyclable/";
    private static final String FOOD           = BASE + "food/";
    private static final String CLASSIFICATION = BASE + "classification/";
    private static final String COMPANIES      = BASE + "collection-company/";
    private static final String BULKY          = "https://clean.chuncheon.go.kr/";

    public WasteGuide fetch() throws Exception {
        Document dGeneral = Jsoup.connect(GENERAL).timeout(15000).get();
        Document dRecy    = Jsoup.connect(RECYCLABLE).timeout(15000).get();
        Document dFood    = Jsoup.connect(FOOD).timeout(15000).get();
        Document dClass   = Jsoup.connect(CLASSIFICATION).timeout(15000).get();
        Document dComp    = Jsoup.connect(COMPANIES).timeout(15000).get();

        // 안정적 요약(공통 안내)
        String hours      = "18:00~23:00 (토·법정공휴일 전일 배출 금지)";
        String locGeneral = "가까운 수거장";
        String locFood    = "아파트(공동수거장), 단독주택/상가(건물 앞)";
        String locBulky   = "배출신고 시 지정 장소";
        String bagColor   = "소각용(불에 타는 쓰레기) 주황색 종량제 봉투";

        Section recyclable = new Section(
                RECYCLABLE,
                pickTitle(dRecy, "재활용품 배출방법"),
                extractParagraphs(dRecy),
                extractBullets(dRecy),
                extractTables(dRecy)
        );
        Section food = new Section(
                FOOD,
                pickTitle(dFood, "음식물쓰레기"),
                extractParagraphs(dFood),
                extractBullets(dFood),
                extractTables(dFood)
        );
        Section classification = new Section(
                CLASSIFICATION,
                pickTitle(dClass, "분리배출 기준"),
                extractParagraphs(dClass),
                extractBullets(dClass),
                extractTables(dClass)
        );

        List<Company> companies = extractCompanies(dComp);

        String updated = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new WasteGuide(
                updated,
                hours, locGeneral, locFood, locBulky, bagColor, BULKY,
                recyclable, food, classification,
                companies
        );
    }

    private String pickTitle(Document doc, String fallback) {
        String h1 = textOrNull(doc.selectFirst("h1, .tit, .title, .page-title, .content-title"));
        return (h1 != null && !h1.isBlank()) ? h1 : fallback;
    }
    private String textOrNull(Element el) { return (el != null) ? el.text().trim() : null; }

    private Element contentRoot(Document doc) {
        Element root = doc.selectFirst("main .contents, main .content, #content, .contents, .content");
        return (root != null) ? root : doc.body();
    }

    // 본문 문단
    private List<String> extractParagraphs(Document doc) {
        Element root = contentRoot(doc);
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (Element p : root.select("p")) addClean(out, p.text());
        // 정의목록(dt/dd)
        for (Element dd : root.select("dl dd")) addClean(out, dd.text());
        // 너무 적으면 strong/b도 포함
        if (out.size() < 4) for (Element s : root.select("strong,b")) addClean(out, s.text());
        return out.stream().map(this::clip).limit(60).collect(Collectors.toList());
    }

    // 리스트
    private List<String> extractBullets(Document doc) {
        Element root = contentRoot(doc);
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (Element li : root.select("ul li, ol li")) addClean(out, li.text());
        if (out.size() < 6) for (String seg : root.text().split("[·•▶▸]")) addClean(out, seg);
        return out.stream().map(this::clip).limit(120).collect(Collectors.toList());
    }

    // 표
    private List<Table> extractTables(Document doc) {
        Element root = contentRoot(doc);
        List<Table> tables = new ArrayList<>();
        for (Element table : root.select("table")) {
            List<String> headers = new ArrayList<>();
            Element thead = table.selectFirst("thead");
            if (thead != null) for (Element th : thead.select("th")) headers.add(th.text().trim());
            if (headers.isEmpty()) {
                Element firstTr = table.selectFirst("tr");
                if (firstTr != null) for (Element thtd : firstTr.select("th,td")) headers.add(thtd.text().trim());
            }

            List<List<String>> rows = new ArrayList<>();
            Elements trs = table.select("tbody tr");
            if (trs.isEmpty()) trs = table.select("tr:gt(0)"); // thead 없을 때
            for (Element tr : trs) {
                List<String> row = new ArrayList<>();
                for (Element c : tr.select("td,th")) row.add(c.text().replaceAll("\\s+"," ").trim());
                if (!row.isEmpty()) rows.add(row);
            }
            boolean has = rows.stream().anyMatch(r -> r.stream().anyMatch(s -> s != null && !s.isBlank()));
            if (has) tables.add(new Table(headers, rows));
        }
        return tables;
    }

    // 수거업체(표 기반에서 유연 매핑)
    private List<Company> extractCompanies(Document doc) {
        List<Company> list = new ArrayList<>();
        for (Table t : extractTables(doc)) {
            int idxName  = indexOfHeader(t.headers(), "업체", "상호", "회사", "업체명");
            int idxPhone = indexOfHeader(t.headers(), "연락처", "전화", "TEL", "전화번호");
            int idxArea  = indexOfHeader(t.headers(), "지역", "담당", "주소", "권역");
            for (List<String> r : t.rows()) {
                String name = pickSafe(r, idxName);
                String phone = pickSafe(r, idxPhone);
                String area = pickSafe(r, idxArea);
                if ((phone == null || phone.isBlank())) for (String s : r) if (looksLikePhone(s)) { phone = s; break; }
                if (!(empty(name) && empty(phone) && empty(area))) list.add(new Company(nz(name), nz(phone), nz(area)));
            }
        }
        return list;
    }

    private void addClean(Set<String> set, String raw) {
        if (raw == null) return;
        String s = raw.replaceAll("\\s+", " ").trim();
        if (s.isBlank()) return;
        s = s.replace("자세히 보기", "").replace("다운로드", "").trim();
        if (s.length() >= 2) set.add(s);
    }
    private String clip(String s){ return (s.length() > 220) ? s.substring(0,220) + "…" : s; }
    private int indexOfHeader(List<String> hs, String... keys){ for(int i=0;i<hs.size();i++){ String h=hs.get(i); for(String k:keys){ if(h!=null && h.contains(k)) return i; } } return -1; }
    private String pickSafe(List<String> row, int idx){ return (idx>=0 && idx<row.size()) ? row.get(idx) : ""; }
    private boolean looksLikePhone(String s){ return s!=null && s.matches(".*\\d{2,3}[-) .]?\\d{3,4}[- .]?\\d{4}.*"); }
    private boolean empty(String s){ return s==null || s.isBlank(); }
    private String nz(String s){ return s==null ? "" : s; }
}
