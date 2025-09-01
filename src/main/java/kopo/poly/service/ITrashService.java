package kopo.poly.service;

import kopo.poly.dto.ReportCreDTO;
import kopo.poly.dto.SolutionDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ITrashService {

    int reportProc(ReportCreDTO rDTO, MultipartFile image) throws Exception;

    int reportSolution(SolutionDTO sDTO, MultipartFile proof) throws Exception;

}
