package com.zrsy.threepig.Util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * 读取配置文件 url: /src/main/resources/application.properties
 */
public class PropertyUtil {
    static Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

    /**
     * 输入key，返回value
     *
     * @param name key
     * @return value
     * @throws IOException
     */
    public static String getProperties(String name) {
        Properties prop = new Properties();
        try {
            prop.load(PropertyUtil.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {

            logger.error("配置文件不存在");
            e.printStackTrace();
        }

        return prop.getProperty(name);
    }


    public static void main(String[] args) throws IOException {

    }

}