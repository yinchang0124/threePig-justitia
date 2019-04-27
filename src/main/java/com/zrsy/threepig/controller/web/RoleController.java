package com.zrsy.threepig.controller.web;

import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.web.implement.RoleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 此类主要是对系统内角色使用智能合约进行管理
 */
@RestController
public class RoleController {
    protected static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    RoleServiceImpl roleService;

    /**
     * 添加角色
     *
     * @param map
     * @return
     */
    @PostMapping("/addRole")
    public ParserResult addRole(@RequestBody Map map) {
        logger.info("接收到添加角色的请求！*****开始添加。");
        return roleService.addRole((Map) map.get("data"));
    }

    /**
     * 获取全部角色信息
     *
     * @return
     */
    @GetMapping("/getAllRole")
    public ParserResult getAllRole() {
        logger.info("接收到获取所有角色信息的请求！*****开始获取。");
        return roleService.getAllRole();
    }

    /**
     * 增加角色的权限和修改上级角色
     *
     * @param map
     * @return
     */
    @PostMapping("/changeRolePowerAndFName")
    public ParserResult changeRolePowerAndFName(@RequestBody Map map) {
        logger.info("接收到增加角色的权限和修改上级角色的请求！*****开始修改。");
        return roleService.changeRolePowerAndFName((Map) map.get("data"));
    }
}
