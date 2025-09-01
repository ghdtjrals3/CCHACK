package kopo.poly.service.impl;

import kopo.poly.dto.UserDTO;
import kopo.poly.mapper.IUserMapper;
import kopo.poly.service.IUserService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;



@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final IUserMapper userMapper;


    @Override
    public String loginProc(UserDTO uDTO) throws Exception {
        log.info(this.getClass().getName() + ".loginproc Service Start!");

        log.info(uDTO.getUser_id());
        log.info(uDTO.getUser_pwd());

        UserDTO resultDTO = userMapper.loginProc(uDTO);
        String msg;

        try {
            if(CmmUtil.nvl(resultDTO.getUser_id()).equals("")) {
                msg = "fail";
            } else {
                msg = "success";
            }
        } catch (Exception e) {
            msg = "fail";
        }
        log.info(this.getClass().getName() + ".loginproc Service End!");
        return msg;
    }


    @Override
    public int signUpProc(UserDTO uDTO) throws Exception {
        log.info(this.getClass().getName() + ".signUpProc Service Start!");

        int result = userMapper.signUpProc(uDTO);

        log.info(this.getClass().getName() + ".signUpProc Service End!");
        return result;
    }

    @Override
    public int checkId(UserDTO uDTO) throws Exception {
        log.info(this.getClass().getName() + ".checkId Service Start!");

        log.info(this.getClass().getName() + ".checkId Service End!");
        return userMapper.checkId(uDTO);
    }


}
