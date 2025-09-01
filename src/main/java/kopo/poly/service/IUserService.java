package kopo.poly.service;

import kopo.poly.dto.UserDTO;

import java.util.List;

public interface IUserService {

    String loginProc(UserDTO uDTO) throws Exception;

    int signUpProc(UserDTO uDTO) throws Exception;

    int checkId(UserDTO uDTO) throws Exception;
}
