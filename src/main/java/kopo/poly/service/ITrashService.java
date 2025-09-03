package kopo.poly.service;

import kopo.poly.dto.ReportCreDTO;
import kopo.poly.dto.SolutionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ITrashService {

    int reportProc(ReportCreDTO rDTO, MultipartFile image) throws Exception;

    int reportSolution(ReportCreDTO rDTO, MultipartFile image) throws Exception;

    List<ReportCreDTO> selectAllTrash() throws Exception;

    List<ReportCreDTO> selectAllReportById(String userId) throws Exception;

    List<ReportCreDTO> selectAllSolutionById(String userId) throws Exception;

    String getDong(String userId) throws Exception;
}
