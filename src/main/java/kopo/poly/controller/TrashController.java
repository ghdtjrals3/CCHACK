package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import kopo.poly.dto.ReportCreDTO;
import kopo.poly.dto.SolutionDTO;
import kopo.poly.dto.UserDTO;
import kopo.poly.service.ITrashService;
import kopo.poly.service.IUserService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.PointUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequestMapping(value = "/trash")
@RequiredArgsConstructor
@Controller
public class TrashController {

    private final ITrashService trashService;

    @PostMapping(value = "reportCreate",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String reportCreate(HttpSession session, ModelMap map, HttpServletRequest request, @RequestParam(name="image_url", required = false) MultipartFile image) throws Exception{
        log.info(this.getClass().getName() + " reportProc Start!!");

        log.info(request.getParameter("description"));
        log.info(request.getParameter("latitude"));
        log.info(request.getParameter("longitude"));
        log.info(image.toString());

        ReportCreDTO rDTO = new ReportCreDTO();

        String user_id = (String) session.getAttribute("user_id");
        Double lan = Double.parseDouble(request.getParameter("latitude"));
        Double lon = Double.parseDouble(request.getParameter("longitude"));
        String title = request.getParameter("title");

        rDTO.setDescription(request.getParameter("description"));
        rDTO.setLat(lan);
        rDTO.setLng(lon);
        rDTO.setStatus("false");
        rDTO.setReporter_id(user_id);
        rDTO.setTitle(title);


        String residence_dong = trashService.getDong(user_id);
        log.info("resi dong : " + residence_dong);

        int point = PointUtil.postMissionScorePoint(1,lan, lon, title, residence_dong);
        log.info("point : " + point);

        rDTO.setPoint(point);



        trashService.reportProc(rDTO,image);



        log.info(this.getClass().getName() + " reportProc End!!");
        return "/trash/report";
    }

    @PostMapping(value="solutionCreate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String reportSolution(HttpSession session, ModelMap map, HttpServletRequest request,  @RequestParam(required=false, name="proof_image_url") MultipartFile proof) throws Exception{
        log.info(this.getClass().getName() + " reportSolution Start!!");

        ReportCreDTO rDTO = new ReportCreDTO();
        rDTO.setNote(request.getParameter("note"));
        rDTO.setReport_id(Long.valueOf(request.getParameter("report_id")));
        rDTO.setResolver_id(session.getAttribute("user_id").toString());

        log.info(rDTO.getReport_id() + "");
        log.info(rDTO.getResolver_id() + "");

        trashService.reportSolution(rDTO,proof);


        log.info(this.getClass().getName() + " reportSolution End!!");
        return "/trash/report";
    }


    @GetMapping(value = "reportPage")
public String reportPage(HttpSession session, Model model) throws Exception {

    log.info(this.getClass().getName() + " reportPage Start!!");

    ReportCreDTO rDTO = new ReportCreDTO();
    // List<ReportCreDTO> rList = trashService.selectAllTrash();

    // model.addAttribute("rList", rList);

    // for (ReportCreDTO rDTO2 : rList) {
    //     log.info("report_id : {}", rDTO2.getReport_id());
    //     log.info("reporter_id : {}", rDTO2.getReporter_id());
    // }

    // // 충돌난 부분을 이렇게 정리
    // log.info("size : {}", rList.size());
    // log.info("id : {}", CmmUtil.nvl((String) session.getAttribute("user_id")));

    log.info(this.getClass().getName() + " reportPage End!!");

    return "/trash/report";
}





}
