package com.sky.config;


import com.sky.properties.ImgProperties;
import com.sky.utils.ImgUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ImgConfig {

    @ConditionalOnMissingBean
    @Bean
    public ImgUtils imgUtils(ImgProperties imgProperties){
        log.info("开始创建imgutils: {}",imgProperties);
        return new ImgUtils(imgProperties.getDefalutLocation(),imgProperties.getFormat());
    }
}
