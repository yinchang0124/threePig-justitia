package com.zrsy.threepig.controller.web;

import com.zrsy.threepig.Util.EmailUtil;
import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.web.UserService;
import com.zrsy.threepig.service.web.implement.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * 此类主要使用智能合约对系统内的用户管理
 */
@RestController
public class UserController {
    protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    EmailUtil emailUtil;
    @Autowired
    UserService userService;

    /**
     * 提交审核信息
     * @param map
     * @return
     */
    @PostMapping("/register")
    public ParserResult register(@RequestBody Map map){
        logger.info("接收到用户注册请求！！！开始进行注册……");
        return userService.register((Map)map.get("data"));
    }


    /**
     *  登录系统，如果登录成功，将password，userid，address写进cookie，前端进行cookie判断。
     * @param map
     * @param response
     * @return
     * @throws IOException
     */
    @PostMapping("/login")
    public ParserResult  login(@RequestBody Map map, HttpServletResponse response) throws IOException {
        logger.info("接收到用户登录的请求！开始进行登录操作……");
        Map map1= (Map) map.get("data");
        ParserResult parserResult=userService.login(map1);
        return parserResult;
    }

    /**
     * 管理员进行用户的提交审核，将信息写入智能合约，写入成功发送邮件通知，并将密钥同时附在邮件中
     * @param map
     * @return
     */
    @PostMapping("/eroll")
    public ParserResult eroll(@RequestBody Map map){
        logger.info("接收到管理员的提交审核的请求！开始提交审核……");
        return userService.eroll((Map)map.get("data"));
    }

    /**
     * 获得系统内所有的用户的信息
     * @return
     */
    @GetMapping("/getAllUser")
    public ParserResult getAllUser(){
        logger.info("接收到获得所有用户信息的请求！开始获取……");
        return userService.getAllUser();
    }

    /**
     * 禁用该用户
     * @param address
     * @return
     */
    @RequestMapping(value = "/banUser/{address}", method = RequestMethod.GET)
    public ParserResult banUser(@PathVariable String address){
        logger.info("接收到管理员禁用用户的请求！开始禁用用户……");
        return userService.banUser(address);
    }



    private String requestUri(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (request.getQueryString() != null) {
            requestUri += ("?" + request.getQueryString());
        }
        return "[" + request.getMethod() + "] " + requestUri;

    }


    private String requestBody(BufferedReader body) {
        String inputLine;
        String bodyStr = "";
        try {
            while ((inputLine = body.readLine()) != null) {
                bodyStr += inputLine;
            }
            body.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        return "[body] " + bodyStr;
    }

}
