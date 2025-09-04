package kopo.poly.dto;

import lombok.*;

import java.time.LocalDateTime;




@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionDTO {
    private Long id;                 // mission_completion.id (bigserial)
    private String userId;           // user_id
    private Long templateId;         // template_id (int8 → Long)
    private String status;           // assigned / completed
    private Integer awardedPoints;   // int4 → Integer
    private String proofImageUrl;
    private String proofNote;
    private LocalDateTime createdAt;
}