package kopo.poly.dto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * lombok은 코딩을 줄이기 위해 @어노테이션을 통한 자동 코드 완성기능임
 *
 * @Getter => getter 함수를 작성하지 않았지만, 자동 생성
 * @Setter => setter 함수를 작성하지 않았지만, 자동 생성
 */
@Getter
@Setter
public class ReportCreDTO {

    private Long report_id;          // PK
    private String reporter_id;      // 신고자
    private String description;      // 설명
    private String image_url;        // 첨부 이미지
    private Double lat;              // 위도
    private Double lng;              // 경도
    private String status;           // 상태 (pending / resolved 등)
    private String title;
    private int point;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String resolver_id;
    private String note;
    private String proof_image_url;
    private LocalDateTime solved_at;

}

