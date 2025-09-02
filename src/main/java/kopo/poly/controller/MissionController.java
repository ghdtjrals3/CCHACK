package kopo.poly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MissionController {

    // http://localhost:11000/mission 으로 접근 가능
    @GetMapping("/mission")
    public String missionPage() {
        // templates/personal/mission.html 렌더링
        return "personal/mission";
    }
}
