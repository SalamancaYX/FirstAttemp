package com.tiefan.frc.dqs.zeus;

import org.springframework.beans.factory.annotation.Value;

/**
 *@ClassName ZeusConfigBean
 *@Description zeus配置实体参数bean
 **/
public class ZeusConfigBean {
    /**
     * 超时时间
     **/
    @Value("${connect.timeout:20}")
    private int connectTimeout;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
