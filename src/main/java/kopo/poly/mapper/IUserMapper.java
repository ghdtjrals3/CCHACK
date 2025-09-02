package kopo.poly.mapper;

import kopo.poly.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserMapper {

    int signUpProc(UserDTO uDTO) throws Exception;

    UserDTO loginProc(UserDTO uDTO) throws Exception;

    int checkId(UserDTO uDTO) throws Exception;

    UserDTO getUserInfo(String user_id) throws Exception;
}
