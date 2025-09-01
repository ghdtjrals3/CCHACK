package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.ReportCreDTO;
import kopo.poly.dto.UserDTO;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequestMapping(value = "/trash")
@RequiredArgsConstructor
@Controller
public class TrashController {

    @PostMapping(value = "reportProc")
    @ResponseBody
    public String reportProc(HttpSession session, ModelMap map, HttpServletRequest request) throws Exception{
        log.info(this.getClass().getName() + " reportProc Start!!");

        ReportCreDTO rDTO = new ReportCreDTO();

        rDTO.setReporter_id(CmmUtil.nvl(request.getParameter("reporter_id")));
        rDTO.setCategory(CmmUtil.nvl(request.getParameter("category")));
        rDTO.setDescription(CmmUtil.nvl(request.getParameter("description")));
        rDTO.setImage_url(CmmUtil.nvl(request.getParameter("image_url")));
        rDTO.setResidence_dong(CmmUtil.nvl(request.getParameter("residence_dong")));

        String latStr = CmmUtil.nvl(request.getParameter("lat"));
        String lngStr = CmmUtil.nvl(request.getParameter("lng"));
        String priorityStr = CmmUtil.nvl(request.getParameter("priority"));

        rDTO.setLat(latStr.isEmpty() ? null : Double.valueOf(latStr));
        rDTO.setLng(lngStr.isEmpty() ? null : Double.valueOf(lngStr));
        rDTO.setPriority(priorityStr.isEmpty() ? 0 : Integer.valueOf(priorityStr));

        rDTO.setStatus("pending");


        log.info(this.getClass().getName() + " reportProc End!!");
        return "";
    }
}
