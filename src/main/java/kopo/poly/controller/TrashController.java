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


        rDTO.setDescription(request.getParameter("description"));
        rDTO.setLat(Double.parseDouble(request.getParameter("latitude")));
        rDTO.setLng(Double.parseDouble(request.getParameter("longitude")));
        rDTO.setStatus("pending");
        rDTO.setReporter_id((CmmUtil.nvl((String) session.getAttribute("user_id"))));
        rDTO.setTitle(request.getParameter("title"));

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
    public String reportPage(HttpSession session, ModelMap model) throws Exception{
        log.info(this.getClass().getName() + " reportPage Start!!");



        ReportCreDTO rDTO = new ReportCreDTO();
        List<ReportCreDTO> rList = trashService.selectAllTrash();

        model.addAttribute("rList",rList);

        for(ReportCreDTO rDTO2 : rList){
            log.info(rDTO2.getReport_id() + "");
            log.info(rDTO2.getReporter_id() + "");
        }

        log.info("id : " + CmmUtil.nvl((String) session.getAttribute("user_id")));

        log.info("size : " + rList.size());

        log.info(this.getClass().getName() + " reportPage End!!");

        return "/trash/report";

    }





}
