// CmmUtil.java
package kopo.poly.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.MissionScoreOut;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PointUtil {
    private static final ObjectMapper OM = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String SCORE_URL = "http://localhost:11000/api/mission/score";

    /**
     * /api/mission/score 호출 후 응답의 point(int)만 반환.
     */
    public static int postMissionScorePoint(long reportId,
                                            double latitude,
                                            double longitude,
                                            String title,
                                            String addr) {
        return postMissionScorePoint(SCORE_URL, reportId, latitude, longitude, title, addr);
    }

    /** URL을 바꾸고 싶을 때 쓰는 오버로드 */
    public static int postMissionScorePoint(String url,
                                            long reportId,
                                            double latitude,
                                            double longitude,
                                            String title,
                                            String addr) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("report_id", reportId);
            payload.put("latitude",  latitude);
            payload.put("longitude", longitude);
            payload.put("title",     title);
            payload.put("addr",      addr);

            String json = OM.writeValueAsString(payload);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Accept",       "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp =
                    CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new RuntimeException("HTTP " + resp.statusCode() + " : " + resp.body());
            }

            JsonNode root = OM.readTree(resp.body());
            if (!root.has("point")) {
                throw new RuntimeException("Response missing 'point': " + resp.body());
            }
            return root.get("point").asInt(); // ← point만 반환
        } catch (Exception e) {
            throw new RuntimeException("postMissionScorePoint failed", e);
        }
    }
}
