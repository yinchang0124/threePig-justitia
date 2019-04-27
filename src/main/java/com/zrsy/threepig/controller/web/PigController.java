package com.zrsy.threepig.controller.web;

import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.web.PigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 此类主要是针对在BigchainDB存取猪的相关信息
 */
@RestController
public class PigController {
    protected static final Logger logger = LoggerFactory.getLogger(PigController.class);

    @Autowired
    PigService pigService;

    /**
     * 增加新猪
     *
     * @param map
     * @return
     */
    @PostMapping("/addpig")
    public ParserResult addPig(@RequestBody Map map) {
        logger.info("接受到添加新猪的请求！******开始添加");
        return pigService.addPig((Map) map.get("data"));
    }

    /**
     * 获得改养殖场中所有的猪的信息
     *
     * @return
     */
    @GetMapping("/getAllPig")
    public ParserResult getAllPig() {
        logger.info("接收到获得养殖场全部猪的信息请求！*****开始获取");
        return pigService.getAllPig();
    }

    @PostMapping("/getPigERC721ID")
    public ParserResult getPigERC721ID(@RequestBody Map map){
        logger.info("接收到获得猪的721ID请求！*****开始获取");
        return pigService.getPigERCID((Map) map.get("data"));
    }



    /**
     * 获得该猪的详细信息
     *
     * @param pigId 猪耳号
     * @return
     */
    @RequestMapping(value = "/getPigInfo/{pigId}", method = RequestMethod.GET)
    public ParserResult getPigInfo(@PathVariable String pigId) {
        logger.info("接收到获取耳号为：" + pigId + "猪的全部信息！****开始获取。");
        return pigService.getPigInfo(pigId);
    }

    /**
     * 获取该猪舍的猪的信息列表
     *
     * @param pigHouseId
     * @return
     */
    @RequestMapping(value = "/getPigList/{pigHouseId}", method = RequestMethod.GET)
    public ParserResult getPigList(@PathVariable String pigHouseId) {
        logger.info("接收到获取猪舍号为：" + pigHouseId + "内的猪的信息列表请求！****开始获取。");
        return pigService.getPigList(pigHouseId);
    }





}
