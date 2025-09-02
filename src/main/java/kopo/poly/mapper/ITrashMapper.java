package kopo.poly.mapper;

import kopo.poly.dto.ReportCreDTO;
import kopo.poly.dto.SolutionDTO;
import kopo.poly.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ITrashMapper {

    int reportProc(ReportCreDTO rDTO) throws Exception;
    int reportSolution(SolutionDTO sDTO) throws Exception;
    int changeReportStatus(long report_id);

    List<ReportCreDTO> selectAllReports(String reporter_id) throws Exception;
    List<SolutionDTO> selectAllSolutions(String resolver_id) throws Exception;

    List<ReportCreDTO> selectAllTrash() throws Exception;
}
