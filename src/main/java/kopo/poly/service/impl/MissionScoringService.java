package kopo.poly.service.impl;

import kopo.poly.dto.MissionScoreIn;
import kopo.poly.dto.MissionScoreOut;
import kopo.poly.service.IMissionScoringService;
import kopo.poly.service.SimpleScore;  // 클래스 이름은 대문자로!
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class MissionScoringService implements IMissionScoringService {

    private final SimpleScore model;

    public MissionScoringService(SimpleScore model) {
        this.model = model;
    }

    @Override
    public MissionScoreOut score(MissionScoreIn in) {
        // 입력 기본 검증
        if (in == null) {
            return new MissionScoreOut(null, 150, "UNKNOWN");
        }
        if (!StringUtils.hasText(in.getTitle())) {
            return new MissionScoreOut(in.getReport_id(), 150, "UNKNOWN");
        }

        // SimpleScore에 위임 (addr과 category만 사용)
        SimpleScore.ScoringResult r = model.score(
                in.getAddr(),   // 주소
                in.getTitle()   // 카테고리
        );

        log.info("dong : {}", r.dong);

        return new MissionScoreOut(in.getReport_id(), r.point, r.dong);
    }
}
