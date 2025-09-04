package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.ReportCreDTO;
import kopo.poly.dto.UserDTO;
import kopo.poly.service.ITrashService;
import kopo.poly.service.IUserService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/*
 * Controller 선언해야만 Spring 프레임워크에서 Controller인지 인식 가능
 * 자바 서블릿 역할 수행
 *
 * slf4j는 스프링 프레임워크에서 로그 처리하는 인터페이스 기술이며,
 * 로그처리 기술인 log4j와 logback과 인터페이스 역할 수행함
 * 스프링 프레임워크는 기본으로 logback을 채택해서 로그 처리함
 * */
@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserController {

    // @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
    private final IUserService userService;
    private final ITrashService trashService;

    /**
     * 게시판 리스트 보여주기
     * <p>
     * GetMapping(value = "notice/noticeList") =>  GET방식을 통해 접속되는 URL이 notice/noticeList 경우 아래 함수를 실행함
     */
    @GetMapping(value = "login")
    public String login(HttpSession session, ModelMap model, HttpServletRequest request) throws Exception {
        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".login Start!");



        log.info(this.getClass().getName() + ".login End!");
        return "user/login";

    }

    //로그인 처리
    @PostMapping(value = "loginProc")
    public String loginProc(HttpSession session, ModelMap model, HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".loginProc Start!");

        String user_id = CmmUtil.nvl(request.getParameter("user_id"));
        String user_pwd = CmmUtil.nvl(request.getParameter("user_pwd"));

        UserDTO uDTO = new UserDTO();

        uDTO.setUser_id(user_id);
        uDTO.setUser_pwd(user_pwd);

        String result = userService.loginProc(uDTO);

        session.setAttribute("user_id", user_id);

        uDTO = null;

        uDTO = userService.getUserInfo(user_id);

        try {
            log.info("user_id : " + uDTO.getUser_id());
        }catch (Exception e){
            return "user/login";
        }

        log.info("test point : " + uDTO.getPoint());
        model.addAttribute("uDTO", uDTO);

        ReportCreDTO rDTO = new ReportCreDTO();
        List<ReportCreDTO> rList = trashService.selectAllTrash();

        model.addAttribute("size", rList.size());

        log.info(this.getClass().getName() + ".loginProc End!");
        return "index";
    }


    @GetMapping(value = "")
    public String index(HttpSession session, ModelMap model, HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".login Start!");

        String user_id = CmmUtil.nvl(session.getAttribute("user_id").toString());

        UserDTO uDTO = new UserDTO();

        uDTO = userService.getUserInfo(user_id);
        List<ReportCreDTO> rList = trashService.selectAllTrash();

        model.addAttribute("size", rList.size());
        model.addAttribute("uDTO", uDTO);

        return "index";
    }

    //회원가입처리
    @PostMapping(value = "signUpProc")
    @ResponseBody
    public String signUpProc(HttpSession session, ModelMap model, HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".signUpProc Start!");

        String user_id = CmmUtil.nvl(request.getParameter("user_id")); // 유저 아이디
        String user_pwd = CmmUtil.nvl(request.getParameter("user_pwd")); // 유저 패스워드
        String user_nick_name = CmmUtil.nvl(request.getParameter("user_nick_name")); // 유저 닉네임
        // 교통 습관
        Boolean commute_by_car = Boolean.valueOf(request.getParameter("commute_by_car")); // 출퇴근 시 자차 이용 여부(예/아니요)
        // 소비 습관
        String cafe_use_freq = CmmUtil.nvl(request.getParameter("cafe_use_freq")); // 카페 이용 빈도 (거의 안 감/ 주1~2회/ 등)
        String grocery_freq = CmmUtil.nvl(request.getParameter("grocery_freq")); // 장보기 빈도 (거의 안 감 / 주1회)
        // 에너지 습관
        Boolean practice_energy_saving = Boolean.valueOf(request.getParameter("practice_energy_saving")); // 에너지 절약 실천 여부
        // 생활 지역
        String residence_dong = CmmUtil.nvl(request.getParameter("residence_dong")); // 거주 행정동
        String workplace_or_school_dong = CmmUtil.nvl(request.getParameter("workplace_or_school_dong")); // 직장/ 학교 행정동



        UserDTO uDTO = new UserDTO();
        uDTO.setUser_id(user_id);
        uDTO.setUser_pwd(user_pwd);
        uDTO.setUser_nick_name(user_nick_name);
        uDTO.setCommute_by_car(commute_by_car);
        uDTO.setCafe_use_freq(cafe_use_freq);
        uDTO.setGrocery_freq(grocery_freq);
        uDTO.setPractice_energy_saving(practice_energy_saving);
        uDTO.setResidence_dong(residence_dong);
        uDTO.setWorkplace_or_school_dong(workplace_or_school_dong);


        int result = userService.signUpProc(uDTO);
        String msg;

        if(result == 0){
            msg = "fail";
            log.info(this.getClass().getName() + "signUpProc Fail!");
        } else {
            msg = "success";
            log.info(this.getClass().getName() + "signUpProc Success!");
        }


        log.info(this.getClass().getName() + ".signUpProc End!");
        // 추후에 있을 페이지 반환
        return msg;
    }

    //중복 아이디 체크를 위한 조회
    @GetMapping(value = "checkId")
    @ResponseBody
    public String checkId(HttpSession session, ModelMap model, HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + "checkId Start!");

        String user_id = CmmUtil.nvl(request.getParameter("user_id"));

        UserDTO uDTO = new UserDTO();
        uDTO.setUser_id(user_id);

        int result = userService.checkId(uDTO);
        String msg = "";

        if(result == 0){
            msg = "available";
        } else  if(result == 1){
            msg = "unavailable";
        }

        log.info(this.getClass().getName() + "checkId End!");
        return msg;
    }

    @GetMapping(value = "myPage")
    public String myPage(HttpSession session, ModelMap model, HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + "myPage Start!");

        String user_id = CmmUtil.nvl((String) session.getAttribute("user_id"));

        List<ReportCreDTO> rList = trashService.selectAllReportById(user_id);
        List<ReportCreDTO> sList = trashService.selectAllSolutionById(user_id);

        UserDTO uDTO = new UserDTO();

        uDTO = userService.getUserInfo(user_id);

        log.info("nick : " + uDTO.getUser_nick_name());


        for(ReportCreDTO reportCreDTO : rList) {
            log.info("repoter_id : " + reportCreDTO.getReporter_id());
            log.info("title : " + reportCreDTO.getTitle());


            log.info("---------------------");
        }


        for(ReportCreDTO reportCreDTO : sList) {
            log.info("resolver_id : " + reportCreDTO.getResolver_id());
            log.info("status : " + reportCreDTO.getStatus());
        }

        model.addAttribute("rList", rList);
        model.addAttribute("sList", sList);
        model.addAttribute("uDTO", uDTO);

        log.info(this.getClass().getName() + "myPage End!");
        return "user/myPage";
    }

    @GetMapping(value = "logOut")
    public String logOut(HttpSession session) throws Exception {
        log.info(this.getClass().getName() + "logOut Start!");
        session.invalidate();
        log.info(this.getClass().getName() + "logOut End!");
        return "index";
    }


}