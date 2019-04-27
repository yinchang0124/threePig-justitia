package com.zrsy.threepig.controller.web;

import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.web.PowerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 此类主要是进行系统的权限管理，通过智能合约控制住
 */
@RestController
public class PowerController {
    protected static final Logger logger = LoggerFactory.getLogger(PowerController.class);
    @Autowired
    PowerService powerService;

    /**
     * 添加权限
     *
     * @param map
     * @return
     */
    @PostMapping("/addPower")
    public ParserResult addPower(@RequestBody Map map) {
        logger.info("接收到添加权限的请求！*****开始添加。");
        return powerService.addPower((Map) map.get("data"));
    }

    /**
     * 获取全部权限信息
     *
     * @return
     */
    @GetMapping("/getPower")
    public ParserResult getPower() {
        logger.info("接收到获取所有权限信息的请求！*****开始获取。");
        return powerService.getPower();
    }

    /**
     * 修改权限信息
     *
     * @param map
     * @return
     */
    @PostMapping("/fixPower")
    public ParserResult fixPower(@RequestBody Map map) {
        logger.info("接收到修改权限信息的请求！*****开始修改。");
        return powerService.fixPower((Map) map.get("data"));
    }

    /**
     * 禁用该权限
     *
     * @param powerId
     * @return
     */
    @GetMapping("/deletePower/{powerId}")
    public ParserResult fixPower(@PathVariable String powerId) {
        logger.info("接收到禁用权限号为：" + powerId + "的请求！*****开始禁用。");
        return powerService.deletePower(powerId);
    }

    /**
     * 获得全部权限号的列表
     *
     * @return
     */
    @GetMapping("/getAllPowerId")
    public ParserResult getAllPowerId() {
        logger.info("接收到获取所有权限号信息的请求！*****开始获取。");
        return powerService.getAllPowerId();
    }


}
