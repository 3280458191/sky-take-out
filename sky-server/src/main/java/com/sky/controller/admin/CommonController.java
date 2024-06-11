package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController         //表示该类为一个待请求处理类
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping ("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        //上传文件到阿里云
        try {
            String originalFilename = file.getOriginalFilename();//原始文件名
            String substring = originalFilename.substring(originalFilename.lastIndexOf("."));   //扩展名
            String upload = aliOssUtil.upload(file.getBytes(), UUID.randomUUID() + substring);  //文件的请求路径
            return Result.success(upload);
        } catch (IOException e) {
            log.error("文件上传失败:{}", e,e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
