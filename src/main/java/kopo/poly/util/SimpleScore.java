// File: SimpleScore.java
package kopo.poly.util;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleScore {

    private Map<String, Double> dongToAccess = new HashMap<>();   // 접근성(0~1, 클수록 쉬움)
    private Map<String, Double> dongToLocDiff = new HashMap<>();  // 위치난이도 = 1 - 접근성

    private static final int[] LABELS = {50, 100, 150, 200, 250, 300};

    private static final Map<String, Double> CATEGORY = new HashMap<>();
    static {
        CATEGORY.put("담배꽁초/작은쓰레기", 0.05);
        CATEGORY.put("캔/병/플라스틱(소량)", 0.15);
        CATEGORY.put("일반생활쓰레기(소형봉투)", 0.25);
        CATEGORY.put("혼합쓰레기(봉투 외)", 0.45);
        CATEGORY.put("음식물쓰레기 유출", 0.65);
        CATEGORY.put("불법투기 가구(스티커 없음)", 0.85);
        CATEGORY.put("건설폐기물/대형폐기물", 0.90);
        CATEGORY.put("위험물(날카로운 물건 등)", 0.95);
    }

    private final double alphaLoc;

    /** 모드 1: CSV에서 접근성 계산 (classpath:/ 또는 파일경로 모두 지원) */
    public SimpleScore(String resCsvLocation, String flowCsvLocation, double alphaLoc) throws Exception {
        this.alphaLoc = alphaLoc;
        loadAndCalibrate(resCsvLocation, flowCsvLocation);
    }

    /** 모드 2: 테스트용 접근성 맵 직접 주입 */
    public SimpleScore(Map<String, Double> dongToAccess, double alphaLoc) {
        this.alphaLoc = alphaLoc;
        setAccessMap(dongToAccess);
    }

    // CSV 로드 및 접근성 산출
    private void loadAndCalibrate(String resCsvLocation, String flowCsvLocation) throws Exception {
        var resRows  = CmmUtil.readCsvAsMaps(resCsvLocation);
        var flowRows = CmmUtil.readCsvAsMaps(flowCsvLocation);

        Map<String, Double> dongToResident = aggregate(resRows, "구분", "전체 인구수(외국인 제외)");
        Map<String, Double> dongToFlow     = aggregate(flowRows, "읍면동", "이용자수");

        Set<String> allDongs = new HashSet<>();
        allDongs.addAll(dongToResident.keySet());
        allDongs.addAll(dongToFlow.keySet());

        Map<String, Double> nr = minmax(logMap(dongToResident), allDongs);
        Map<String, Double> nf = minmax(logMap(dongToFlow),     allDongs);

        final double wResident = 0.4;
        final double wFlow     = 0.6;

        Map<String, Double> access = new HashMap<>();
        for (String d : allDongs) {
            double a = wResident * nr.getOrDefault(d, 0.0)
                    + wFlow     * nf.getOrDefault(d, 0.0);
            access.put(d, a); // 0~1
        }
        setAccessMap(access);
    }

    private void setAccessMap(Map<String, Double> access) {
        this.dongToAccess = new HashMap<>(access);
        this.dongToLocDiff = access.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> 1.0 - e.getValue()));
    }

    // === Public API ===
    public ReportResult scoreReport(String reportId, String addr, String title) {
        String dong = normalizeDongFromAddr(addr);
        if (dong == null || dong.isBlank()) dong = "UNKNOWN";

        double locDiff = dongToLocDiff.getOrDefault(dong, avgLocDiff());
        double catDiff = CATEGORY.getOrDefault(title, 0.4);

        double combined = clamp01(alphaLoc * locDiff + (1.0 - alphaLoc) * catDiff);
        int point = toDiscretePoint(combined);

        return new ReportResult(reportId, addr, title, dong, point, locDiff, catDiff, combined);
    }

    public int scoreReport(String addr, String title) {
        String dong = normalizeDongFromAddr(addr);
        if (dong == null || dong.isBlank()) dong = "UNKNOWN";

        double locDiff = dongToLocDiff.getOrDefault(dong, avgLocDiff());
        double catDiff = CATEGORY.getOrDefault(title, 0.4);

        double combined = clamp01(alphaLoc * locDiff + (1.0 - alphaLoc) * catDiff);
        return toDiscretePoint(combined);
    }

    public List<DongAccess> getAccessibilityRanking() {
        return dongToAccess.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(e -> new DongAccess(e.getKey(), e.getValue(), 1.0 - e.getValue()))
                .toList();
    }

    // === 내부 유틸 ===
    private Map<String, Double> aggregate(List<Map<String, String>> rows, String keyCol, String valCol) {
        Map<String, Double> out = new HashMap<>();
        for (Map<String, String> r : rows) {
            String key = normalizeDong(r.getOrDefault(keyCol, ""));
            if (key == null || key.isBlank()) continue;
            String v = r.getOrDefault(valCol, "0").replace(",", "").trim();
            double d = 0.0;
            try { d = Double.parseDouble(v); } catch (Exception ignored) {}
            out.merge(key, d, Double::sum);
        }
        return out;
    }

    private Map<String, Double> logMap(Map<String, Double> m) {
        Map<String, Double> out = new HashMap<>();
        for (var e : m.entrySet()) {
            double x = Math.log1p(Math.max(e.getValue(), 0.0));
            out.put(e.getKey(), x);
        }
        return out;
    }

    private Map<String, Double> minmax(Map<String, Double> m, Set<String> keys) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        for (String k : keys) {
            double v = m.getOrDefault(k, 0.0);
            if (v < min) min = v;
            if (v > max) max = v;
        }
        Map<String, Double> out = new HashMap<>();
        if (Double.compare(min, max) == 0) {
            for (String k : keys) out.put(k, 0.0);
            return out;
        }
        double range = max - min;
        for (String k : keys) {
            double v = m.getOrDefault(k, 0.0);
            out.put(k, (v - min) / range);
        }
        return out;
    }

    private double avgLocDiff() {
        return dongToLocDiff.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.5);
    }

    private static double clamp01(double x) {
        if (x < 0) return 0;
        if (x > 1) return 1;
        return x;
    }

    private int toDiscretePoint(double combined) {
        int idx = (int) Math.floor(combined * LABELS.length);
        if (idx >= LABELS.length) idx = LABELS.length - 1;
        return LABELS[idx];
    }

    // 주소 → 동명 추정(치환 포함)
    private String normalizeDongFromAddr(String addr) {
        if (addr == null) return null;
        String s = addr.trim().replace(" ", "");
        s = normalizeDong(s);
        int idxDong = Math.max(s.lastIndexOf("동"), Math.max(s.lastIndexOf("읍"), s.lastIndexOf("면")));
        if (idxDong >= 0) {
            int start = idxDong;
            while (start > 0) {
                char c = s.charAt(start - 1);
                if (c < '가' || c > '힣') break;
                start--;
            }
            String candidate = s.substring(start, idxDong + 1);
            return normalizeDong(candidate);
        }
        return s;
    }

    private String normalizeDong(String name) {
        if (name == null) return null;
        String s = name.trim().replace(" ", "");
        Map<String, String> rep = Map.of(
                "소양로1가", "소양동", "소양로2가", "소양동", "소양로3가", "소양동",
                "중앙로1가", "약사명동", "중앙로2가", "약사명동", "중앙로3가", "약사명동",
                "약사동",   "약사명동"
        );
        return rep.getOrDefault(s, s);
    }

    // DTO
    public static class ReportResult {
        public final String reportId, addr, title, dong;
        public final int point;
        public final double locDiff, catDiff, combined;
        public ReportResult(String reportId, String addr, String title,
                            String dong, int point, double locDiff, double catDiff, double combined) {
            this.reportId = reportId; this.addr = addr; this.title = title;
            this.dong = dong; this.point = point; this.locDiff = locDiff; this.catDiff = catDiff; this.combined = combined;
        }
        @Override public String toString() {
            return String.format(Locale.ROOT, "reportId=%s, dong=%s, point=%d, locDiff=%.2f, catDiff=%.2f, combined=%.2f",
                    reportId, dong, point, locDiff, catDiff, combined);
        }
    }

    public static class DongAccess {
        public final String dong;
        public final double access, locDiff;
        public DongAccess(String dong, double access, double locDiff) {
            this.dong = dong; this.access = access; this.locDiff = locDiff;
        }
    }
}
