package kopo.poly.dto;
import lombok.Getter;
import lombok.Setter;

/**
 * lombok은 코딩을 줄이기 위해 @어노테이션을 통한 자동 코드 완성기능임
 *
 * @Getter => getter 함수를 작성하지 않았지만, 자동 생성
 * @Setter => setter 함수를 작성하지 않았지만, 자동 생성
 */
@Getter
@Setter
public class UserDTO {


    private String user_id; // 유저 아이디
    private String user_pwd; // 유저 패스워드
    private String user_nick_name; // 유저 닉네임

    // 교통 습관
    private Boolean commute_by_car; // 출퇴근 시 자차 이용 여부(예/아니요)

    // 소비 습관
    private String cafe_use_freq; // 카페 이용 빈도 (거의 안 감/ 주1~2회/ 등)
    private String grocery_freq; // 장보기 빈도 (거의 안 감 / 주1회)

    // 에너지 습관
    private Boolean practice_energy_saving; // 에너지 절약 실천 여부

    // 생활 지역
    private String residence_dong; // 거주 행정동
    private String workplace_or_school_dong; // 직장/ 학교 행정동

}

