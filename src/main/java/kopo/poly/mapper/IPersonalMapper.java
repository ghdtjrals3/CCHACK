package kopo.poly.mapper;

import kopo.poly.dto.MissionCardDTO;
import kopo.poly.dto.MissionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface IPersonalMapper {

    void goMisson(MissionDTO mDTO) throws Exception;




    Long insertMissionStart(@Param("userId") String userId,
                            @Param("templateId") Long templateId,
                            @Param("startLat") Double startLat,
                            @Param("startLng") Double startLng,
                            @Param("startAt") OffsetDateTime startAt);

    Long findActiveMissionId(@Param("userId") String userId,
                             @Param("templateId") Long templateId);

    // ↓↓↓ 권한 제거: userId 파라미터 삭제
    MissionDTO completeMissionAndDetectMode(@Param("id") Long id,
                                            @Param("endLat") Double endLat,
                                            @Param("endLng") Double endLng,
                                            @Param("endAt")  OffsetDateTime endAt);

    Map<String,Object> verifyAndAwardPoints(@Param("id") Long id);

    List<MissionCardDTO> listMissionCards(@Param("userId") String userId,
                                          @Param("limit") int limit,
                                          @Param("offset") int offset);

    MissionDTO getMissionById(@Param("id") Long id);

    int startTransport(String userId, Long templateId, double lat, double lng, OffsetDateTime startAt);
}
