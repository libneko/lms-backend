package com.neko.controller.admin;

import com.neko.constant.MessageConstant;
import com.neko.result.Result;
import com.neko.utils.AWSS3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class AdminCommonController {

    private final AWSS3Util awsS3Util;

    public AdminCommonController(AWSS3Util awsS3Util) {
        this.awsS3Util = awsS3Util;
    }

    @PostMapping("/upload")
    public Result<Object> upload(MultipartFile file) {
        log.info("File upload: {}", file);

        try {
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID() + ext;

            String filePath = awsS3Util.uploadBookImage(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error(MessageConstant.UPLOAD_FAILED, e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
