package com.zrsy.threepig.service.hardware;

import com.zrsy.threepig.domain.ParserResult;

import java.util.Map;

public interface IEnvService {
    ParserResult setEnv(Map map);

    ParserResult getEnv();
}
