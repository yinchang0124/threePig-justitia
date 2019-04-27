package com.zrsy.threepig.service.hardware.implement;

import com.zrsy.threepig.BDQL.BDQLUtil;
import com.zrsy.threepig.BigchainDB.BigchainDBUtil;
import com.zrsy.threepig.domain.BDQL.BigchainDBData;
import com.zrsy.threepig.domain.BDQL.Table;
import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.hardware.IConnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConnServiceImpl implements IConnService {


    /**
     * 获得BigchainDB中的Raspberry表中的数据
     *
     * @return
     */
    @Override
    public List<Map> getAllRaspberry() {
        ParserResult result = BDQLUtil.work("Select * from Raspberry");
        Table table = (Table) result.getData();
        List<Map> list = table.getData();
        List<Map> reslut = new ArrayList<>();
        if (list != null) {
            for (Map map : list) {
                Map map1 = new HashMap();
                map1.put("PiMac", map.get("PiMac").toString());
                map1.put("PiIp", map.get("PiIp").toString());
                map1.put("PiStatus", "已注册");
                map1.put("assetId", map.get("TXID").toString());
                reslut.add(map1);
            }
        }

        return reslut;
    }


    /**
     * 创建Raspberry表
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult createRaspberryAsset(Map map) {
        ParserResult parserResult = new ParserResult();
        BigchainDBData data = new BigchainDBData("Raspberry", map);
        String id = null;
        try {
            id = BigchainDBUtil.createAsset(data);
        } catch (Exception e) {
            e.printStackTrace();
            parserResult.setStatus(ParserResult.ERROR);
            parserResult.setMessage("fail");
            return parserResult;
        }
        if (BigchainDBUtil.checkTransactionExit(id)) {
            parserResult.setMessage("success");
            parserResult.setStatus(ParserResult.SUCCESS);
            parserResult.setData(id);
            return parserResult;
        }
        parserResult.setStatus(ParserResult.ERROR);
        parserResult.setMessage("fail");
        return parserResult;
    }
}
