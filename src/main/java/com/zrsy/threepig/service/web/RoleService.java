package com.zrsy.threepig.service.web;

import com.zrsy.threepig.domain.ParserResult;

import java.util.Map;

public interface RoleService {
    ParserResult addRole(Map map);

    ParserResult getAllRole();

    ParserResult changeRolePowerAndFName(Map map);
}
