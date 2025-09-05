package kopo.poly.service;

import kopo.poly.dto.MissionCardDTO;
import kopo.poly.dto.MissionDTO;
import kopo.poly.dto.ReportCreDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public interface IPersonalService {

    void goMisson(MissionDTO mDTO, MultipartFile image) throws  Exception;



    Long start(String userId, Long templateId, double lat, double lng, OffsetDateTime startAt);
    MissionDTO complete(String userId, Long assignmentId, double lat, double lng, OffsetDateTime endAt, boolean autoVerify);
    Map<String,Object> verify(Long assignmentId); // status, awarded_points, detected_mode, algo_expected
    List<MissionCardDTO> listCards(String userId, int page, int size);

}
