package com.zrsy.threepig.service.web.implement;

import com.zrsy.threepig.BDQL.BDQLUtil;
import com.zrsy.threepig.BigchainDB.BigchainDBUtil;
import com.zrsy.threepig.Contract.PIG.Pig;
import com.zrsy.threepig.Util.ContractUtil;
import com.zrsy.threepig.Util.EthereumUtil;
import com.zrsy.threepig.Util.FileUtil;
import com.zrsy.threepig.domain.BDQL.BigchainDBData;
import com.zrsy.threepig.domain.BDQL.Table;
import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.web.PigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PigServiceImpl implements PigService {
    protected static final Logger logger = LoggerFactory.getLogger(PigServiceImpl.class);

    @Autowired
    ContractUtil contractUtil;

    @Autowired
    EthereumUtil ethereumUtil;

    @Autowired
    FileUtil fileUtil;


    /**
     * 增加新猪的信息
     *
     * @param info
     * @return
     */
    @Override
    public ParserResult addPig(Map info) {
        ParserResult parserResult = new ParserResult();
        BigchainDBData bigchainDBData = new BigchainDBData("pigInfo", info);
        logger.info("要增加的猪的信息   " + info.toString());
        String assetID;
        try {
            assetID = BigchainDBUtil.createAsset(bigchainDBData);
        } catch (Exception e) {
            parserResult.setStatus(ParserResult.ERROR);
            parserResult.setMessage("error");
            e.printStackTrace();
            return parserResult;
        }
        logger.info("创建资产成功，资产ID：" + assetID);
        logger.info("添加新猪成功");
        for(;true;){
            if(BigchainDBUtil.checkTransactionExit(assetID)){
                break;
            }
        }

        // 制作status表
        Map map = new HashMap();
        map.put("earId", info.get("earId").toString());
        map.put("tokenId", info.get("tokenId").toString());
        map.put("statu", "0");

        bigchainDBData = new BigchainDBData("pigStatus", map);
        String txID = BigchainDBUtil.transferToSelf(bigchainDBData, assetID);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        parserResult.setStatus(ParserResult.SUCCESS);
        parserResult.setMessage("success");
        return parserResult;


    }

    /**
     * 获得猪场内全部猪的信息
     *
     * @return
     */
    @Override
    public ParserResult getAllPig() {
        ParserResult parserResult = new ParserResult();
        logger.info("使用BDQL开始查询，BDQL语句：select * from pigInfo");
        parserResult = BDQLUtil.work("select * from pigInfo");
        Table table = (Table) parserResult.getData();
        logger.info("查询结果：" + table.toString());
        parserResult.setData(table.getData());
        return parserResult;
    }

    /**
     * 通过BDQL查询该猪的信息
     *
     * @param earId 猪earID
     * @return
     */
    @Override
    public ParserResult getPigInfo(String earId) {
        ParserResult parserResult = new ParserResult();
        logger.info("使用BDQL开始查询，BDQL语句：select * from pigInfo where earId =" + earId);
        parserResult = BDQLUtil.work("select * from pigInfo where earId =" + earId);
        Table table = (Table) parserResult.getData();
        logger.info("查询结果：" + table.toString());
        parserResult.setData(table.getData());
        logger.info(parserResult.getData().toString());
        return parserResult;
    }


    /**
     * 获得该猪舍内猪的信息列表
     *
     * @param pigHouseId 猪舍号
     * @return
     */
    @Override
    public ParserResult getPigList(String pigHouseId) {
        logger.info("使用BDQL开始查询，BDQL语句：select earId,breed,column,ringNumber,matingWeek,remarks from pigInfo where pigstyId =" + pigHouseId);
        ParserResult parserResult = BDQLUtil.work("select earId,breed,column,ringNumber,matingWeek,remarks from pigInfo where pigstyId =" + pigHouseId);
        Table table = (Table) parserResult.getData();
        logger.info("查询结果：" + table.toString());
        parserResult.setData(table.getData());
        logger.info(parserResult.getData().toString());
        return parserResult;
    }

    /**
     * 合约创建猪，获得tokenID
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult getPigERCID(Map map) {
        logger.info("开始在合约创建猪，并返回721id");
        ParserResult parserResult = new ParserResult();
        Pig pig = contractUtil.PigLoad(map.get("address").toString());
        if (ethereumUtil.UnlockAccount(map.get("address").toString(), map.get("password").toString())) {
            logger.info("解锁成功！！");
            TransactionReceipt transactionReceipt = null;
            try {

                transactionReceipt = pig.createPig(map.get("breed").toString(), new BigInteger(map.get("earId").toString()), new BigInteger(map.get("pigHouse").toString())).send();
                List<Pig.Log_BirthEventResponse> responses = pig.getLog_BirthEvents(transactionReceipt);
                int tokenId = responses.get(0).tokenId.intValue();
                parserResult.setData(tokenId + "");
                parserResult.setStatus(ParserResult.SUCCESS);
                parserResult.setMessage("success");
                return parserResult;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parserResult.setStatus(ParserResult.ERROR);
        parserResult.setData(null);
        parserResult.setMessage("fail");
        return parserResult;
    }


    public static void main(String[] args) throws UnknownHostException, SocketException {
        InetAddress ia = InetAddress.getLocalHost();
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        System.out.println("mac数组长度：" + mac.length);
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
            System.out.println("每8位:" + str);
            if (str.length() == 1) {
                sb.append("0" + str);
            } else {
                sb.append(str);
            }
        }
        System.out.println("本机MAC地址:" + sb.toString().toUpperCase());
    }
}