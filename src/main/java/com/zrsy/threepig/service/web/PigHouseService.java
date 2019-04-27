package com.zrsy.threepig.service.web;

import com.zrsy.threepig.domain.ParserResult;

import java.util.Map;

public interface PigHouseService {
    ParserResult addPigHouse(Map map);

    ParserResult getPigHouseList();

    ParserResult getPigHouseIDList();

    ParserResult getPigHouseEnv(String pigSty);
}
