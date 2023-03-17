package com.ean.reggie.controller;

import com.ean.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @description:TODO
 * @author:Povlean
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    public String basePath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info(file.toString());

        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf("."));

        String fileName = UUID.randomUUID().toString() + suffix;

        File dir = new File(basePath);

        if(!dir.exists()){
           dir.mkdirs();
        }

        try{
            file.transferTo(new File(basePath + fileName));
        }catch (IOException e){
            e.printStackTrace();
        }
        return Result.success(fileName);
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response,String name){
        try {
            // 输入流，通过输入流读取文件内容
            FileInputStream fis = new FileInputStream(new File(basePath + name));
            // 输出流，通过输出流将文件写到浏览器，在浏览器中展示图片
            ServletOutputStream ops = response.getOutputStream();
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024 * 2];
            while( (len = fis.read(bytes)) != -1 ){
                ops.write(bytes,0,len);
                ops.flush();
            }
            fis.close();
            ops.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
