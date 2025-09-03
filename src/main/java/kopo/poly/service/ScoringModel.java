package kopo.poly.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ScoringModel {

    private final String resCsvPath;
    private final String flowCsvPath;
    private final double alphaLoc;
    private final String kakaoKey;
    private final String kakaoUrl;

    private final WebClient webClient; // 선택

    // 접근성/난이도 테이블 (행정동 → 값)
    private Map<String, Double> dongToAccess = new HashMap<>(); // 접근성지수
    private Map<String, Double> dongToLocDiff = new HashMap<>(); // 위치난이도(=1-접근성)
    // 경계 (정규분포 유사 분위수 7개 값: 0%, 2.35% ... 100%)
    private double[] thresholds;

    // 50~300 라벨
    private static final int[] LABELS = { 50, 100, 150, 200, 250, 300 };

    // 카테고리 난이도 맵(0~1)
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

    public ScoringModel(
            @Value("${scoring.res-csv}") String resCsvPath,
            @Value("${scoring.flow-csv}") String flowCsvPath,
            @Value("${scoring.alpha-loc:0.6}") double alphaLoc,
            @Value("${kakao.rest-key:}") String kakaoKey,
            @Value("${kakao.coord2addr-url:https://dapi.kakao.com/v2/local/geo/coord2address.json}") String kakaoUrl,
            WebClient.Builder webClientBuilder) throws Exception {
        this.resCsvPath = resCsvPath;
        this.flowCsvPath = flowCsvPath;
        this.alphaLoc = alphaLoc;
        this.kakaoKey = kakaoKey;
        this.kakaoUrl = kakaoUrl;
        this.webClient = webClientBuilder.build();

        loadAndCalibrate(); // 시작 시 한번 로드
    }

    // 외부에서 재로딩 원할 때 호출
    public synchronized void reload() throws Exception {
        loadAndCalibrate();
    }

    private void loadAndCalibrate() throws Exception {
        // 1) CSV 읽기
        List<Map<String, String>> resRows = readCsvAsMaps(resCsvPath);
        List<Map<String, String>> flowRows = readCsvAsMaps(flowCsvPath);

        // 2) 행정동별 집계
        Map<String, Double> dongToResident = aggregate(resRows, "구분", "전체 인구수(외국인 제외)");
        Map<String, Double> dongToHousehold = aggregate(resRows, "구분", "세대수");
        Map<String, Double> dongToFlow = aggregate(flowRows, "읍면동", "이용자수");

        // 3) 접근성 계산
        Set<String> allDongs = new HashSet<>();
        allDongs.addAll(dongToResident.keySet());
        allDongs.addAll(dongToHousehold.keySet());
        allDongs.addAll(dongToFlow.keySet());

        // 로그 변환 + min-max 정규화
        Map<String, Double> logResident = logMap(dongToResident);
        Map<String, Double> logHouse = logMap(dongToHousehold);
        Map<String, Double> logFlow = logMap(dongToFlow);

        Map<String, Double> nr = minmax(logResident, allDongs);
        Map<String, Double> nh = minmax(logHouse, allDongs);
        Map<String, Double> nf = minmax(logFlow, allDongs);

        Map<String, Double> access = new HashMap<>();
        for (String d : allDongs) {
            double a = 0.4 * nr.getOrDefault(d, 0.0)
                    + 0.5 * nf.getOrDefault(d, 0.0)
                    + 0.1 * nh.getOrDefault(d, 0.0);
            access.put(d, a);
        }
        this.dongToAccess = access;

        // 위치난이도 = 1 - 접근성
        Map<String, Double> locDiff = access.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> 1.0 - e.getValue()));
        this.dongToLocDiff = locDiff;

        // 4) 경계(정규분포 유사 분위수) 캘리브레이션
        double[] probs = new double[] { 0.0, 0.0235, 0.1585, 0.5, 0.8415, 0.9765, 1.0 };
        double[] values = locDiff.values().stream().mapToDouble(Double::doubleValue).sorted().toArray();
        this.thresholds = quantiles(values, probs);
        enforceStrictIncrease(this.thresholds, 1e-9);
    }

    // ----------------- 공개 API -----------------
    public ScoringResult score(double lat, double lon, String category, String addrOptional) {
        // 1) 행정동 추정 (우선순위: 입력 addrOptional > 카카오 역지오코딩 > UNKNOWN)
        String dong = normalizeDong(addrOptional);
        if (dong == null || dong.isBlank()) {
            dong = reverseGeocode(lat, lon); // null 허용
        }
        if (dong == null)
            dong = "UNKNOWN";

        // 2) 위치난이도 조회 (없으면 평균값)
        double locDiff = dongToLocDiff.getOrDefault(dong, avgLocDiff());

        // 3) 카테고리 난이도
        double catDiff = CATEGORY.getOrDefault(category, 0.4);

        // 4) 결합난이도
        double combined = alphaLoc * locDiff + (1 - alphaLoc) * catDiff;

        // 5) 점수 이산화 (50~300)
        int point = digitize(combined, thresholds, LABELS);

        return new ScoringResult(point, dong, locDiff, catDiff, combined);
    }

    // ----------------- 내부 유틸 -----------------
    private List<Map<String, String>> readCsvAsMaps(String pathOrClasspath) throws Exception {
        Resource resource;

        if (pathOrClasspath.startsWith("classpath:")) {
            String rel = pathOrClasspath.substring("classpath:".length());
            resource = new ClassPathResource(rel);
            if (!resource.exists()) {
                throw new IllegalArgumentException("CSV not found on classpath: " + rel);
            }
        } else {
            Path p = Path.of(pathOrClasspath);
            if (!Files.exists(p)) {
                throw new IllegalArgumentException("CSV not found: " + p.toAbsolutePath());
            }
            resource = new org.springframework.core.io.FileSystemResource(p);
        }

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        try (var in = resource.getInputStream();
                var br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            MappingIterator<Map<String, String>> it = mapper.readerFor(Map.class).with(schema).readValues(br);
            return it.readAll();
        }
    }

    private Map<String, Double> aggregate(List<Map<String, String>> rows, String keyCol, String valCol) {
        Map<String, Double> out = new HashMap<>();
        for (Map<String, String> r : rows) {
            String key = normalizeDong(r.getOrDefault(keyCol, ""));
            if (key == null || key.isBlank())
                continue;
            String v = r.getOrDefault(valCol, "0").replace(",", "").trim();
            double d = 0.0;
            try {
                d = Double.parseDouble(v);
            } catch (Exception ignored) {
            }
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
            if (v < min)
                min = v;
            if (v > max)
                max = v;
        }
        Map<String, Double> out = new HashMap<>();
        if (Double.compare(min, max) == 0) {
            for (String k : keys)
                out.put(k, 0.0);
            return out;
        }
        double range = max - min;
        for (String k : keys) {
            double v = m.getOrDefault(k, 0.0);
            out.put(k, (v - min) / range);
        }
        return out;
    }

    private double[] quantiles(double[] sorted, double[] probs) {
        double[] q = new double[probs.length];
        int n = sorted.length;
        for (int i = 0; i < probs.length; i++) {
            double p = probs[i];
            if (n == 0) {
                q[i] = 0.0;
                continue;
            }
            double pos = p * (n - 1);
            int lo = (int) Math.floor(pos);
            int hi = (int) Math.ceil(pos);
            if (lo == hi)
                q[i] = sorted[lo];
            else {
                double w = pos - lo;
                q[i] = sorted[lo] * (1 - w) + sorted[hi] * w;
            }
        }
        return q;
    }

    private void enforceStrictIncrease(double[] a, double eps) {
        for (int i = 1; i < a.length; i++) {
            if (a[i] <= a[i - 1])
                a[i] = a[i - 1] + eps;
        }
    }

    private int digitize(double v, double[] th, int[] labels) {
        // th 길이 = labels 길이 + 1
        int idx = Arrays.binarySearch(th, v);
        if (idx < 0)
            idx = -idx - 2; // insertion point - 1
        idx = Math.max(0, Math.min(idx, labels.length - 1));
        return labels[idx];
    }

    private double avgLocDiff() {
        return dongToLocDiff.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.5);
    }

    private String normalizeDong(String name) {
        if (name == null)
            return null;
        String s = name.trim().replace(" ", "");
        Map<String, String> rep = Map.of(
                "소양로1가", "소양동", "소양로2가", "소양동", "소양로3가", "소양동",
                "중앙로1가", "약사명동", "중앙로2가", "약사명동", "중앙로3가", "약사명동",
                "약사동", "약사명동");
        return rep.getOrDefault(s, s);
    }

    private String reverseGeocode(double lat, double lon) {
        if (kakaoKey == null || kakaoKey.isBlank())
            return null;
        try {
            String json = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(kakaoUrl.replace("https://dapi.kakao.com", "")) // 절대경로면 그대로 쓰이지만, baseUrl 없을 때 안전.
                            .queryParam("x", Double.toString(lon))
                            .queryParam("y", Double.toString(lat))
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 매우 단순 파싱(정식 파싱 원하면 Jackson으로 DTO 만들어도 됨)
            if (json != null && json.contains("region_3depth_name")) {
                int i = json.indexOf("region_3depth_name");
                int start = json.indexOf(':', i) + 1;
                int q1 = json.indexOf('"', start);
                int q2 = json.indexOf('"', q1 + 1);
                String v = json.substring(q1 + 1, q2);
                return normalizeDong(v);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    // 응답 상세(디버그/모니터링용)
    public static class ScoringResult {
        public final int point;
        public final String dong;
        public final double locDiff;
        public final double catDiff;
        public final double combined;

        public ScoringResult(int point, String dong, double locDiff, double catDiff, double combined) {
            this.point = point;
            this.dong = dong;
            this.locDiff = locDiff;
            this.catDiff = catDiff;
            this.combined = combined;
        }
    }
}
