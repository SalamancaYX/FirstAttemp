package com.tiefan.frc.dqs.fsp;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 *@ClassName PlaceHolder
 *@Description 属性解析
 **/
public class PlaceHolder extends PropertyPlaceholderConfigurer {

    @Override
    protected void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
        Set<String> sets = props.stringPropertyNames();
        for (String s : sets) {
            if (s.startsWith("fdg")) {
                System.setProperty(s, props.getProperty(s));
            }
        }
    }
}
