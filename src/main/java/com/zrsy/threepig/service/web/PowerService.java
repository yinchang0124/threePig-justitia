package com.zrsy.threepig.service.web;

import com.zrsy.threepig.domain.ParserResult;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

public interface PowerService {
    ParserResult addPower(Map map);

    ParserResult getPower();

    ParserResult fixPower(Map map);

    ParserResult deletePower(String powerId);

    ParserResult getAllPowerId();
}
