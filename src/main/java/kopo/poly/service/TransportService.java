package kopo.poly.service;

import kopo.poly.util.TransportDetector;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransportService {

    private static class State {
        TransportDetector detector = new TransportDetector(60.0, 5.0);
        String lastStableMode = "unknown";
        double lastT = 0.0;

        // 대중교통 지속 판정용
        boolean onTransit = false;
        Double transitEnterT = null; // 버스/지하철로 들어온 시각(sec)
        double transitAccumSec = 0.0; // 누적(원하면 사용)
    }

    private final Map<String, State> devices = new ConcurrentHashMap<>();
    private static final double TRANSIT_CONFIRM_SEC = 60.0; // 최소 60초 지속 시 "탐승 중"으로 인정

    private State getState(String deviceId){
        return devices.computeIfAbsent(deviceId, k -> new State());
    }

    public TransportDetector.Result push(String deviceId, double t, double lat, double lon,
                                         Double accuracy, Double speed, Double bearing) {
        State st = getState(deviceId);
        var r = st.detector.push(t, lat, lon, accuracy, speed, bearing);
        if (r != null) {
            st.lastStableMode = r.mode;
            st.lastT = t;

            boolean isTransitMode = "bus".equals(r.mode) || "subway".equals(r.mode);

            if (isTransitMode) {
                if (st.transitEnterT == null) {
                    st.transitEnterT = t;
                }
                double dur = t - st.transitEnterT;
                st.onTransit = (dur >= TRANSIT_CONFIRM_SEC);
                st.transitAccumSec = Math.max(st.transitAccumSec, dur);
            } else {
                // 대중교통 상태 이탈 시 초기화
                st.onTransit = false;
                st.transitEnterT = null;
            }
        }
        return r;
    }

    public boolean isOnTransit(String deviceId) {
        State st = devices.get(deviceId);
        return st != null && st.onTransit;
    }

    public String lastMode(String deviceId){
        State st = devices.get(deviceId);
        return (st == null) ? "unknown" : st.lastStableMode;
    }

    public void reset(String deviceId){
        devices.remove(deviceId);
    }
}
