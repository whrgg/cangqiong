package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.ImgUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

@RestController
@Slf4j
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    ImgUtils imgUtils;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){

        String filename = null;
        try {
            filename = imgUtils.Upload(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

        return Result.success(filename);
    }



}
