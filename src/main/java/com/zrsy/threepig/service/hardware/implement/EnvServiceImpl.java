package com.zrsy.threepig.service.hardware.implement;

import com.alibaba.fastjson.JSON;
import com.zrsy.threepig.Util.FileUtil;
import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.hardware.IEnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class EnvServiceImpl implements IEnvService {
    @Autowired
    FileUtil fileUtil;

    /**
     * 设置环境信息范围
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult setEnv(Map map) {
        ParserResult parserResult=new ParserResult();

        if (fileUtil.writeFile("./env.json", map)) {
            parserResult.setMessage("success");
            parserResult.setStatus(ParserResult.SUCCESS);
            return parserResult;
        }else{
            parserResult.setMessage("fail");
            parserResult.setStatus(ParserResult.ERROR);
            return parserResult;
        }

    }

    /**
     * 获取环境信息范围
     * @return
     */
    @Override
    public ParserResult getEnv() {
        ParserResult parserResult=new ParserResult();
        String json=fileUtil.readFile("./env.json");
        Map map= (Map) JSON.parse(json);
        parserResult.setStatus(ParserResult.SUCCESS);
        parserResult.setMessage("success");
        parserResult.setData(map);
        return parserResult;
    }
}
