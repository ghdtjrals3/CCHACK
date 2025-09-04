package kopo.poly.dto;

import java.util.Map;

public class TransportResponseDTO {
    public String deviceId;
    public String mode;
    public String modeRaw;
    public double confidence;
    public Map<String, Double> features;
}
