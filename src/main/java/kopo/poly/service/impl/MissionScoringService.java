package kopo.poly.service.impl;

import kopo.poly.dto.MissionScoreIn;
import kopo.poly.dto.MissionScoreOut;
import kopo.poly.service.IMissionScoringService;
import kopo.poly.service.ScoringModel;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MissionScoringService implements IMissionScoringService {

    private final ScoringModel model;

    public MissionScoringService(ScoringModel model) {
        this.model = model;
    }

    @Override
    public MissionScoreOut score(MissionScoreIn in) {
        // 입력 기본 검증(컨트롤러 @Valid와 중복되지만 방어적으로 한 번 더 확인)
        if (in == null) {
            return new MissionScoreOut(null, 150, "UNKNOWN");
        }
        if (in.getLatitude() == null || in.getLongitude() == null || !StringUtils.hasText(in.getTitle())) {
            return new MissionScoreOut(in.getReport_id(), 150, "UNKNOWN");
        }

        // ScoringModel에 위임 (addr가 있으면 역지오코딩 생략)
        ScoringModel.ScoringResult r = model.score(
                in.getLatitude(),
                in.getLongitude(),
                in.getTitle(),
                in.getAddr()
        );

        // 필요한 필드만 응답으로 매핑
        return new MissionScoreOut(in.getReport_id(), r.point, r.dong);
    }
}
