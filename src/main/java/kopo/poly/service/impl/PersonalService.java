package kopo.poly.service.impl;

import kopo.poly.dto.MissionDTO;
import kopo.poly.dto.ReportCreDTO;
import kopo.poly.mapper.IPersonalMapper;
import kopo.poly.mapper.ITrashMapper;
import kopo.poly.service.IPersonalService;
import kopo.poly.service.ITrashService;
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
public class PersonalService implements IPersonalService {

    private final IPersonalMapper personalMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Override
    public void goMisson(MissionDTO mDTO, MultipartFile image) throws Exception {

        if (image != null && !image.isEmpty()) {
            String url = CmmUtil.saveFile(image, uploadDir, "mission"); // C:/uploads/report/ → /uploads/report/...
            mDTO.setProofImageUrl(url);
        }
        personalMapper.goMisson(mDTO);
    }
}
