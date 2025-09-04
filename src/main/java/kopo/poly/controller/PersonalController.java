package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MissionCardDTO;
import kopo.poly.dto.MissionDTO;
import kopo.poly.mapper.IMissionMapper;
import kopo.poly.mapper.IPersonalMapper;
import kopo.poly.mapper.IUserMapper;
import kopo.poly.service.IPersonalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/personal")
public class PersonalController {

    private final IMissionMapper missionMapper;
    private final IPersonalService personalService;

    // 개인미션 페이지
    @GetMapping("/mission")
    public String personalMission(HttpSession session, Model model) throws Exception {
        log.info(this.getClass().getName() + " psernal controller Start !!");

        String userId = session.getAttribute("user_id").toString();
        // 1) 없으면 채우기 (위 CTE 호출)
        // 1) 없으면 7개 배정
        missionMapper.fillIfEmpty(userId, 7);

        // 2) 저장된 목록 조회
        List<MissionCardDTO> missions = missionMapper.findAssignedWithTemplate(userId);
        model.addAttribute("missions", missions);



        log.info(this.getClass().getName() + " psernal controller End !!");
        return "personal/mission";
    }


    @PostMapping(value = "goMission")
    @ResponseBody
    public String goMission(HttpServletRequest request, HttpSession session, Model model, @RequestParam(name="image", required = false) MultipartFile image) throws Exception {


        MissionDTO mDTO = new MissionDTO();
        mDTO.setProofNote(request.getParameter("proof_note"));
        mDTO.setUserId(session.getAttribute("user_id").toString());
        mDTO.setTemplateId(Long.valueOf(request.getParameter("templateId")));

        personalService.goMisson(mDTO, image);

        log.info(mDTO.getProofNote());
        log.info(mDTO.getUserId());
        log.info(mDTO.getTemplateId().toString());




        return "";
    }
}
