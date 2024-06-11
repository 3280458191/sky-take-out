package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {

    /**
     * 阿里云配置项
     */
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
