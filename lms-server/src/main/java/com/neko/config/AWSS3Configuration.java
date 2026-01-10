package com.neko.config;

import com.neko.properties.AWSS3Properties;
import com.neko.utils.AWSS3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AWSS3Configuration {

    @Bean
    @ConditionalOnMissingBean
    public AWSS3Util awsS3Util(AWSS3Properties awss3Properties) {
        return new AWSS3Util(awss3Properties.getEndpoint(),
                awss3Properties.getAccessKeyId(),
                awss3Properties.getAccessKeySecret(),
                awss3Properties.getBucketName());
    }
}
