package kopo.poly.service;

import kopo.poly.dto.MissionScoreIn;
import kopo.poly.dto.MissionScoreOut;

public interface IMissionScoringService {
    MissionScoreOut score(MissionScoreIn in);
}
