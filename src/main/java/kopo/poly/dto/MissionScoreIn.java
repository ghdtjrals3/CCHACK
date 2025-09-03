package kopo.poly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MissionScoreIn {   // 대문자 i
    @NotBlank
    private String report_id;   // = trash_id
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotBlank
    private String title;       // = category
    private String addr;        // 선택: 프론트가 행정동 알고 있으면 넣기

    // getters/setters
    public String getReport_id() { return report_id; }
    public void setReport_id(String report_id) { this.report_id = report_id; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAddr() { return addr; }
    public void setAddr(String addr) { this.addr = addr; }
}
