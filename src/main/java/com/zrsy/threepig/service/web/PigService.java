package com.zrsy.threepig.service.web;

import com.zrsy.threepig.domain.ParserResult;

import java.util.Map;

public interface PigService {
    ParserResult addPig(Map info);

    ParserResult getAllPig();

    ParserResult getPigInfo(String earId);

    ParserResult getPigList(String pigHouseId);

    ParserResult getPigERCID(Map map);

}
