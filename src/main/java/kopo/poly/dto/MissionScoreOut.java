package kopo.poly.dto;

public class MissionScoreOut {
    private String report_id;
    private Integer point;  // 50~300
    private String addr;    // 행정동

    public MissionScoreOut() {}
    public MissionScoreOut(String report_id, Integer point, String addr) {
        this.report_id = report_id; this.point = point; this.addr = addr;
    }

    public String getReport_id() { return report_id; }
    public void setReport_id(String report_id) { this.report_id = report_id; }
    public Integer getPoint() { return point; }
    public void setPoint(Integer point) { this.point = point; }
    public String getAddr() { return addr; }
    public void setAddr(String addr) { this.addr = addr; }
}
