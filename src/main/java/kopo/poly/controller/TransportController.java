package kopo.poly.controller;

import kopo.poly.dto.GpsPointDTO;
import kopo.poly.dto.TransportResponseDTO;
import kopo.poly.service.TransportService;
import kopo.poly.util.TransportDetector;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transport")
public class TransportController {

    private final TransportService svc;

    public TransportController(TransportService svc) {
        this.svc = svc;
    }

    // 1) 포인트 푸시 + 즉시 판정 반환
    @PostMapping("/push")
    public TransportResponseDTO push(@RequestBody GpsPointDTO p) {
        TransportDetector.Result r = svc.push(p.deviceId, p.t, p.lat, p.lon, p.accuracy, p.speed, p.bearing);

        TransportResponseDTO resp = new TransportResponseDTO();
        resp.deviceId = p.deviceId;

        if (r != null) {
            resp.mode = r.mode;
            resp.modeRaw = r.modeRaw;
            resp.confidence = r.confidence;
            resp.features = r.features;
        } else {
            resp.mode = "pending";
            resp.modeRaw = "pending";
            resp.confidence = 0.0;
            resp.features = null;
        }
        return resp;
    }

    // 2) 현재 “대중교통 상태인지”만 간단 조회
    @GetMapping("/transit-status")
    public String transitStatus(@RequestParam String deviceId){
        boolean on = svc.isOnTransit(deviceId);
        String mode = svc.lastMode(deviceId);
        // 아주 단순한 응답(프론트에서 다루기 쉬움)
        return "{\"onTransit\":" + on + ",\"mode\":\"" + mode + "\"}";
    }

    @PostMapping("/reset")
    public void reset(@RequestParam String deviceId) {
        svc.reset(deviceId);
    }
}
