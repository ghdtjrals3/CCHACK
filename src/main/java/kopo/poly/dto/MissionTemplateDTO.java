package kopo.poly.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionTemplateDTO {
    private Long id;
    private String code;
    private String title;
    private String verifyType;   // photo | algo
    private String algoKey;
    private String algoExpected;
    private Integer points;
}