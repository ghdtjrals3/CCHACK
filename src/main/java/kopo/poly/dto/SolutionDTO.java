package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SolutionDTO {

    private Long   solution_id;
    private Long   report_id;
    private String resolver_id;
    private String result;             // SUCCESS / FAIL
    private String note;
    private String proof_image_url;    // 저장 경로
    private Instant solved_at;

}
