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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

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
        mDTO.setAwardedPoints(Integer.valueOf((request.getParameter("awardedPoints"))));

        personalService.goMisson(mDTO, image);

        log.info(mDTO.getProofNote());
        log.info(mDTO.getUserId());
        log.info(mDTO.getTemplateId().toString());

        return "";
    }







    // 세션에서 user_id 꺼내는 헬퍼 (프로젝트 방식에 맞게 교체)
    private String currentUser(HttpSession session) {
        Object uid = session.getAttribute("user_id");
        return uid.toString();
    }


    // 1) 도전하기
    @PostMapping("/start")
    public ResponseEntity<Map<String,Object>> start(
            HttpSession session,
            @RequestParam Long templateId,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime at) {

        String userId = currentUser(session);
        Long assignmentId = personalService.start(userId, templateId, lat, lng, at);
        return ResponseEntity.ok(Map.of("assignmentId", assignmentId));
    }

    // 2) 인증하기
    @PostMapping("/complete")
    public ResponseEntity<MissionDTO> complete(
            HttpSession session,
            @RequestParam Long assignmentId,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime at,
            @RequestParam(defaultValue = "true") boolean autoVerify) {

        String userId = currentUser(session);
        MissionDTO dto = personalService.complete(userId, assignmentId, lat, lng, at, autoVerify);
        return ResponseEntity.ok(dto);
    }

    // 3) (선택) 관리자/수동 검증용
    @PostMapping("/verify")
    public ResponseEntity<Map<String,Object>> verify(@RequestParam Long assignmentId) {
        return ResponseEntity.ok(personalService.verify(assignmentId));
    }

    // 4) 카드 목록
    @GetMapping("/cards")
    public ResponseEntity<List<MissionCardDTO>> listCards(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String userId = currentUser(session);
        return ResponseEntity.ok(personalService.listCards(userId, page, size));
    }




}
