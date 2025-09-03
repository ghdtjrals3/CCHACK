package kopo.poly.mapper;

import kopo.poly.dto.ReportCreDTO;
import kopo.poly.dto.SolutionDTO;
import kopo.poly.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ITrashMapper {

    int reportProc(ReportCreDTO rDTO) throws Exception;
    int reportSolution(ReportCreDTO rDTO) throws Exception;
    int changeReportStatus(long report_id);


    List<ReportCreDTO> selectAllTrash() throws Exception;
    List<ReportCreDTO> selectAllReportById(String user_id) throws Exception;
    List<ReportCreDTO> selectAllSolutionById(String userId) throws Exception;
}
