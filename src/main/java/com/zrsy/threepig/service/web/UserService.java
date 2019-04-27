package com.zrsy.threepig.service.web;

import com.zrsy.threepig.domain.ParserResult;

import java.util.Map;

public interface UserService {
    ParserResult register(Map map);

    ParserResult login(Map map);

    ParserResult getAllUser();

    ParserResult eroll(Map map);

    ParserResult banUser(String address);
}
