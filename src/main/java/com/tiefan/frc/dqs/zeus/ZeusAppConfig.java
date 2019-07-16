package com.tiefan.frc.dqs.zeus;

import com.ctrip.framework.apollo.spring.annotation.EnableZeusConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *@ClassName ZeusAppConfig
 *@Description zeus配置类
 **/
@Configuration
@EnableZeusConfig
public class ZeusAppConfig {
    @Bean
    public ZeusConfigBean getZeusConfigBean() {
        return new ZeusConfigBean();
    }
}
