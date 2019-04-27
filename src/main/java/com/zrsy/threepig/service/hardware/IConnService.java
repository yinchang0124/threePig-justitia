package com.zrsy.threepig.service.hardware;

import com.zrsy.threepig.domain.ParserResult;

import java.util.List;
import java.util.Map;

public interface IConnService {

    List<Map> getAllRaspberry();

    ParserResult createRaspberryAsset(Map map);


}
