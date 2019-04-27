package com.zrsy.threepig.controller.hardware;

import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.hardware.IEnvService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
public class EnvController {
    protected static final Logger logger = LoggerFactory.getLogger(EnvController.class);

    @Autowired
    IEnvService envService;

    @PostMapping("/setEnv")
    public ParserResult setEnv(@RequestBody Map map){
        logger.info("接收到设置环境范围的请求……");
        return envService.setEnv((Map)map.get("data"));
    }

    @GetMapping("/getEnv")
    public  ParserResult getEnv(){
        logger.info("接收到获取环境范围的请求………………");
        return envService.getEnv();
    }
}
