package kopo.poly.service.impl;

import kopo.poly.dto.ReportCreDTO;
import kopo.poly.dto.SolutionDTO;
import kopo.poly.dto.UserDTO;
import kopo.poly.mapper.ITrashMapper;
import kopo.poly.mapper.IUserMapper;
import kopo.poly.service.ITrashService;
import kopo.poly.service.IUserService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class TrashService implements ITrashService {

    private final ITrashMapper trashMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public int reportProc(ReportCreDTO rDTO, MultipartFile image) throws Exception {
        log.info(this.getClass().getName() + "TrashService.reportProc Start!!");

        if (image != null && !image.isEmpty()) {
            String url = CmmUtil.saveFile(image, uploadDir, "report"); // C:/uploads/report/ → /uploads/report/...
            rDTO.setImage_url(url);
        }

        log.info(this.getClass().getName() + "TrashService.reportProc End!!");
        return trashMapper.reportProc(rDTO);
    }

    @Override
    public int reportSolution(ReportCreDTO rDTO, MultipartFile image) throws Exception {
        log.info(this.getClass().getName() + "TrashService.reportSolution Start!!");

        if (image != null && !image.isEmpty()) {
            String url = CmmUtil.saveFile(image, uploadDir, "solution"); // C:/uploads/report/ → /uploads/report/...
            rDTO.setProof_image_url(url);
        }

        log.info(this.getClass().getName() + "TrashService.reportSolution End!!");
        return trashMapper.reportSolution(rDTO);
    }

    @Override
    public List<ReportCreDTO> selectAllTrash() throws Exception {
        log.info(this.getClass().getName() + "selectAllTrash Start!!");
        log.info(this.getClass().getName() + "selectAllTrash End!!");

        return trashMapper.selectAllTrash();
    }


}
