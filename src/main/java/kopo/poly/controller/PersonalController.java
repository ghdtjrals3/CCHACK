package kopo.poly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PersonalController {

    // 개인미션 페이지
    @GetMapping("/personal/mission")
    public String personalMission() {
        return "personal/mission"; // -> templates/personal/mission.html
    }
}
