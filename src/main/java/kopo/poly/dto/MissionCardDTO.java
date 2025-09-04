package kopo.poly.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionCardDTO {
    private Long assignmentId;       // completion.id
    private Long templateId;
    private String status;
    private Integer awardedPoints;

    private String code;
    private String title;
    private String verifyType;
    private String algoKey;
    private String algoExpected;
    private Integer points;          // 템플릿 기준 포인트
}