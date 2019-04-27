package com.zrsy.threepig.controller.web;

import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.web.PigHouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 此类主要是BigchainDB上的存取猪舍的相关信息
 */
@RestController
public class PigHouseController {

    protected static final Logger logger = LoggerFactory.getLogger(PigHouseController.class);

    @Autowired
    PigHouseService pigHouseService;

    /**
     * 增加猪舍
     *
     * @param map
     * @return
     */
    @PostMapping("/addPighouse")
    public ParserResult addPigHouse(@RequestBody Map map) {
        logger.info("接收到添加猪舍信息的请求！*****开始添加");
        return pigHouseService.addPigHouse((Map) map.get("data"));
    }

    /**
     * 获得所有猪舍信息
     *
     * @return
     */
    @GetMapping("/pigHouseList")
    public ParserResult getPigHouseList() {
        logger.info("接收到获取所有猪舍信息的请求！*****开始获取。");
        return pigHouseService.getPigHouseList();
    }

    /**
     * 获取所有猪舍号列表
     *
     * @return
     */
    @GetMapping("/pigHouseIdList")
    public ParserResult getPighouseIDList() {
        logger.info("接收到获取所有猪舍号列表的请求！*****开始获取。");
        return pigHouseService.getPigHouseIDList();
    }

    @GetMapping("/getPigHouseEnv/{id}")
    public ParserResult getPigHouseEnv(@PathVariable String id){
        logger.info("");

        return  pigHouseService.getPigHouseEnv(id);
    }
}
