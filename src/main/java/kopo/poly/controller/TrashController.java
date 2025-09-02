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

@Slf4j
@RequestMapping(value = "/trash")
@RequiredArgsConstructor
@Controller
public class TrashController {

    private final ITrashService trashService;

    @PostMapping(value = "reportProc",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String reportProc(HttpSession session, ModelMap map, HttpServletRequest request, @RequestParam(name="image", required = false) MultipartFile image) throws Exception{
        log.info(this.getClass().getName() + " reportProc Start!!");

        ReportCreDTO rDTO = new ReportCreDTO();

        rDTO.setReporter_id(CmmUtil.nvl(request.getParameter("reporter_id")));
        rDTO.setCategory(CmmUtil.nvl(request.getParameter("category")));
        rDTO.setDescription(CmmUtil.nvl(request.getParameter("description")));
        rDTO.setResidence_dong(CmmUtil.nvl(request.getParameter("residence_dong")));

        String latStr = CmmUtil.nvl(request.getParameter("lat"));
        String lngStr = CmmUtil.nvl(request.getParameter("lng"));
        String priorityStr = CmmUtil.nvl(request.getParameter("priority"));

        rDTO.setLat(latStr.isEmpty() ? null : Double.valueOf(latStr));
        rDTO.setLng(lngStr.isEmpty() ? null : Double.valueOf(lngStr));
        rDTO.setPriority(priorityStr.isEmpty() ? 0 : Integer.valueOf(priorityStr));

        rDTO.setStatus("pending");

        int result = trashService.reportProc(rDTO, image);
        String msg;

        if(result == 0){
            msg = "fail";
            log.info(this.getClass().getName() + "signUpProc Fail!");
        } else {
            msg = "success";
            log.info(this.getClass().getName() + "signUpProc Success!");
        }


        log.info(this.getClass().getName() + " reportProc End!!");
        return msg;
    }

    @PostMapping(value="solution", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String reportSolution(HttpSession session, ModelMap map, HttpServletRequest request,  @RequestParam(required=false, name="proof") MultipartFile proof) throws Exception{
        log.info(this.getClass().getName() + " reportSolution Start!!");

        SolutionDTO sDTO = new SolutionDTO();
        sDTO.setSolution_id(Long.valueOf(request.getParameter("reporter_id")));
        sDTO.setReport_id(Long.valueOf(request.getParameter("category")));
        sDTO.setResolver_id(CmmUtil.nvl(request.getParameter("resolver_id")));
        sDTO.setResult(CmmUtil.nvl(request.getParameter("residence_dong")));
        sDTO.setNote(CmmUtil.nvl(request.getParameter("description")));
        sDTO.setProof_image_url(CmmUtil.nvl(request.getParameter("description")));

        int result = trashService.reportSolution(sDTO, proof);

        log.info(this.getClass().getName() + " reportSolution End!!");
        return "";
    }


    @GetMapping(value = "reportPage")
    public String reportPage(HttpSession session, ModelMap model) throws Exception{
        log.info(this.getClass().getName() + " reportPage Start!!");
        log.info(this.getClass().getName() + " reportPage End!!");

        return "/trash/report";

    }





}
