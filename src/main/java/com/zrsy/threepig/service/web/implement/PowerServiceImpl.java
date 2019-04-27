package com.zrsy.threepig.service.web.implement;

import com.zrsy.threepig.Contract.RBAC.User;
import com.zrsy.threepig.Util.ContractUtil;
import com.zrsy.threepig.Util.EthereumUtil;
import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.web.PowerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.tuples.generated.Tuple4;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PowerServiceImpl 将权利的管理再合约上实现。
 */
@Service
public class PowerServiceImpl implements PowerService {
    protected static final Logger logger = LoggerFactory.getLogger(PowerServiceImpl.class);

    /**
     * 智能合约工具类
     */
    @Autowired
    ContractUtil contractUtil;

    /**
     * 以太坊geth客户端工具类
     */
    @Autowired
    EthereumUtil ethereumUtil;
    /**
     * 管理员账户
     */
    @Value("${account_address}")
    private String root_address;

    /**
     * 增加权限，需要先解锁管理员的账户，然后调用智能合约
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult addPower(Map map) {
        ParserResult parserResult = new ParserResult();
        logger.info("需要增加的权限信息："+map);
        User user = contractUtil.UserLoad();
        try {
            ethereumUtil.UnlockAccount(root_address,"11111111");
            user.addPower(new BigInteger(map.get("powerId").toString()), map.get("powerName").toString(), map.get("powerInfo").toString()).send();
            parserResult.setMessage("success");
            parserResult.setStatus(ParserResult.SUCCESS);
            logger.info("增加权限成功！！！！" + map.toString());
        } catch (Exception e) {
            e.printStackTrace();
            parserResult.setMessage("fail");
            parserResult.setStatus(ParserResult.ERROR);
            logger.error("增加权限失败！！！！" + map.toString());
        }

        return parserResult;
    }

    /**
     * 获得全部权利信息
     *
     * @return
     */
    @Override
    public ParserResult getPower() {
        ParserResult parserResult = new ParserResult();
        User user = contractUtil.UserLoad();
        List<BigInteger> list = new ArrayList();
        try {
            list = user.getAllPowerID().send();
            logger.info("获得全部权限ID成功！！" + list.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获得全部权限ID失败！！");
            parserResult.setStatus(ParserResult.ERROR);
            parserResult.setMessage("获得全部权限ID失败！！");
            return parserResult;
        }
        List<Map> data = new ArrayList<>();
        for (BigInteger bigInteger : list) {
            Tuple4<BigInteger, String, String, Boolean> tuple4 = null;
            try {
                tuple4 = user.getPowerInfoBypowerId(bigInteger).send();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("获取权限号为" + bigInteger.toString() + "的权限信息失败！！！");
            }
            Map map = new HashMap();
            map.put("powerId", tuple4.getValue1().toString());
            map.put("powerName", tuple4.getValue2());
            map.put("powerInfo", tuple4.getValue3());
            if (tuple4.getValue4()) {
                map.put("powerStatus", "使用中");
            }
            data.add(map);
        }
        parserResult.setStatus(ParserResult.SUCCESS);
        parserResult.setMessage("获取权限信息成功");
        parserResult.setData(data);
        return parserResult;

    }

    /**
     * 修改权限信息
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult fixPower(Map map) {
        logger.info("开始修改权限信息，信息："+map);
        ParserResult parserResult = new ParserResult();
        User user = contractUtil.UserLoad();
        try {
            user.changePowerInfo(new BigInteger(map.get("powerId").toString()), map.get("powerInfo").toString()).send();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("修改权利号：" + map.get("powerId").toString() + "的权利信息失败！！" + map.toString());
            parserResult.setMessage("修改权利号：" + map.get("powerId").toString() + "的权利信息失败！！" + map.toString());
            parserResult.setStatus(ParserResult.ERROR);
            return parserResult;
        }
        try {
            user.changePowername(new BigInteger(map.get("powerId").toString()), map.get("powerName").toString()).send();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("修改权利号：" + map.get("powerId").toString() + "的权利名称失败！！" + map.toString());
            parserResult.setMessage("修改权利号：" + map.get("powerId").toString() + "的权利名称失败！！" + map.toString());
            parserResult.setStatus(ParserResult.ERROR);
            return parserResult;
        }
        parserResult.setStatus(ParserResult.SUCCESS);
        parserResult.setMessage("success");
        return parserResult;
    }

    /**
     * 禁用权限
     * @param powerId
     * @return
     */
    @Override
    public ParserResult deletePower(String powerId) {
        logger.info("开始禁用权限id为"+powerId+"信息！！");
        ParserResult parserResult=new ParserResult();
        User user = contractUtil.UserLoad();
        try {
            user.changeUnUse(new BigInteger(powerId)).send();
        } catch (Exception e) {
            e.printStackTrace();
            parserResult.setMessage("禁用权限失败！！！");
            parserResult.setStatus(ParserResult.ERROR);
            logger.error("禁用权限:"+powerId+"失败！！！");
            return parserResult;
        }
        parserResult.setStatus(ParserResult.SUCCESS);
        parserResult.setMessage("success");
        logger.info("禁用权限:"+powerId+"成功！！！");
        return parserResult;
    }

    /**
     * 获得所有权限的权限ID
     * @return
     */
    @Override
    public ParserResult getAllPowerId() {
        logger.info("开始获取全部的权限ID！！");
        ParserResult parserResult=new ParserResult();
        User user = contractUtil.UserLoad();
        List<BigInteger> list=null;
        try {
            list=user.getAllPowerID().send();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取全部权限ID失败");
            parserResult.setMessage("获取全部权限ID失败");
            parserResult.setStatus(ParserResult.ERROR);
            return parserResult;
        }
        parserResult.setStatus(ParserResult.SUCCESS);
        parserResult.setMessage("success");
        parserResult.setData(list);
        logger.info("获取全部ID信息，"+list.toString());
        return parserResult;
    }
}
