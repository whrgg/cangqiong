package com.sky.utils;


import com.sky.result.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ImgUtils {

    private String defalutLocation;
    private String format;

    public String Upload(MultipartFile file) throws FileNotFoundException {

        File dir =new File(defalutLocation);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String filename = file.getOriginalFilename();
        filename=filename.split("\\.")[0];
        String ImgName =defalutLocation+ filename +UUID.randomUUID().toString() +format;

        File imgfile =new File(ImgName);
        try {
            file.transferTo(imgfile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return ImgName;
    }
}
