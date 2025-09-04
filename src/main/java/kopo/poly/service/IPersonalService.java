package kopo.poly.service;

import kopo.poly.dto.MissionDTO;
import kopo.poly.dto.ReportCreDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPersonalService {

    void goMisson(MissionDTO mDTO, MultipartFile image) throws  Exception;

}
