package kopo.poly.service.impl;

import kopo.poly.dto.MissionCardDTO;
import kopo.poly.dto.MissionDTO;
import kopo.poly.dto.ReportCreDTO;
import kopo.poly.mapper.IPersonalMapper;
import kopo.poly.mapper.ITrashMapper;
import kopo.poly.service.IPersonalService;
import kopo.poly.service.ITrashService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Service
public class PersonalService implements IPersonalService {

    private final IPersonalMapper personalMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Override
    public void goMisson(MissionDTO mDTO, MultipartFile image) throws Exception {

        if (image != null && !image.isEmpty()) {
            String url = CmmUtil.saveFile(image, uploadDir, "mission"); // C:/uploads/report/ → /uploads/report/...
            mDTO.setProofImageUrl(url);
        }
        personalMapper.goMisson(mDTO);
    }











    @Transactional
    @Override
    public Long start(String userId, Long templateId, double lat, double lng, OffsetDateTime startAt) {
        Long active = personalMapper.findActiveMissionId(userId, templateId);
        validateLatLng(lat, lng);
        if (active != null) {
            int rows = personalMapper.startTransport(userId, templateId, lat, lng, startAt);
            log.info("startTransport rows={}", rows); // 1이어야 정상
            return active;
        }
        Long id = personalMapper.insertMissionStart(userId, templateId, lat, lng, startAt);
        if (id == null) throw new IllegalStateException("미션 시작 저장 실패");
        return id;
    }

    @Transactional
    @Override
    public MissionDTO complete(String userId, Long assignmentId, double lat, double lng,
                               OffsetDateTime endAt, boolean autoVerify) {

        validateLatLng(lat, lng);

        // 권한 체크 제거: userId 안 씀
        MissionDTO updated = personalMapper.completeMissionAndDetectMode(
                assignmentId, lat, lng, endAt);

        if (updated == null) {
            // 권한 문구 제거
            throw new IllegalArgumentException("진행 중 미션이 없습니다.");
        }

        if (autoVerify) personalMapper.verifyAndAwardPoints(assignmentId);
        return personalMapper.getMissionById(assignmentId);
    }

    @Transactional
    @Override
    public Map<String, Object> verify(Long assignmentId) {
        Map<String,Object> r = personalMapper.verifyAndAwardPoints(assignmentId);
        if (r == null) throw new IllegalArgumentException("검증 대상이 없거나 템플릿을 찾지 못했습니다.");
        return r;
    }

    @Override
    public List<MissionCardDTO> listCards(String userId, int page, int size) {
        int limit = Math.max(1, Math.min(50, size));
        int offset = Math.max(0, page) * limit;
        return personalMapper.listMissionCards(userId, limit, offset);
    }

    private void validateLatLng(double lat, double lng) {
        if (Double.isNaN(lat) || Double.isNaN(lng) || Math.abs(lat) > 90 || Math.abs(lng) > 180)
            throw new IllegalArgumentException("위치 좌표가 유효하지 않습니다.");
    }

}
