package kopo.poly.mapper;

import kopo.poly.dto.MissionCardDTO;
import kopo.poly.dto.MissionDTO;
import kopo.poly.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IMissionMapper {

    /** 없으면 랜덤 N개 배정(있으면 아무것도 안 함) */
    int fillIfEmpty(@Param("userId") String userId, @Param("n") int n);

    /** 배정된 목록(템플릿 조인) */
    List<MissionCardDTO> findAssignedWithTemplate(@Param("userId") String userId);

    /** 사진 인증 완료 처리(assigned → completed) */
    int completeWithPhoto(@Param("assignmentId") Long assignmentId,
                          @Param("userId") String userId,
                          @Param("proofImageUrl") String proofImageUrl,
                          @Param("proofNote") String proofNote);

    /** (선택) user 포인트 적립 – users 테이블 있을 때만 사용 */
    int addUserPointFromCompletion(@Param("assignmentId") Long assignmentId,
                                   @Param("userId") String userId);
}
