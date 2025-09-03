package kopo.poly.controller;

import jakarta.validation.Valid;
import kopo.poly.dto.MissionScoreIn;
import kopo.poly.dto.MissionScoreOut;
import kopo.poly.service.IMissionScoringService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mission")
public class MissionScoreController {

    private final IMissionScoringService scoringService;

    public MissionScoreController(IMissionScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping(
            value = "/score",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public MissionScoreOut score(@Valid @RequestBody MissionScoreIn in) {
        return scoringService.score(in);
    }
}
