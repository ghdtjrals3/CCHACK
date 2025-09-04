package kopo.poly.mapper;

import kopo.poly.dto.MissionCardDTO;
import kopo.poly.dto.MissionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface IPersonalMapper {

    void goMisson(MissionDTO mDTO) throws Exception;
}
