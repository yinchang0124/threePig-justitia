package com.zrsy.threepig.service.web.implement;

import com.zrsy.threepig.Contract.RBAC.User;
import com.zrsy.threepig.Util.ContractUtil;
import com.zrsy.threepig.Util.EthereumUtil;
import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.web.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.tuples.generated.Tuple3;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RoleServiceImpl 将角色的管理再合约上实现。
 */
@Service
public class RoleServiceImpl implements RoleService {
    protected static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    /**
     * 智能合约工具类
     */
    @Autowired
    ContractUtil contractUtil;

    /**
     * 以太坊geth工具类
     */
    @Autowired
    EthereumUtil ethereumUtil;

    /**
     * 管理员地址
     */
    @Value("${account_address}")
    private String root_address;

    /**
     * 增加角色
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult addRole(Map map) {
        ParserResult parserResult = new ParserResult();
        logger.info("开始增加角色！角色信息：" + map.toString());
        User user = contractUtil.UserLoad();
        String a = map.get("roleId").toString();
        try {
            ethereumUtil.UnlockAccount(root_address, "11111111");
            user.createRole(new BigInteger(a), map.get("roleFName").toString(), map.get("roleName").toString()).send();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("增加角色失败，角色信息：" + map.toString());
            parserResult.setStatus(ParserResult.ERROR);
            parserResult.setMessage("增加角色失败，角色信息：" + map.toString());
            return parserResult;
        }
        logger.info("成功增加角色信息");
        parserResult.setMessage("success");
        parserResult.setStatus(ParserResult.SUCCESS);
        return parserResult;
    }

    /**
     * 获得全部信息 先查询角色个数，循环查出每个角色的详细信息
     *
     * @return
     */
    @Override
    public ParserResult getAllRole() {
        logger.info("开始获取全部角色信息");
        ParserResult parserResult = new ParserResult();
        User user = contractUtil.UserLoad();
        BigInteger count = null;
        try {
            count = user.getRoleCount().send();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询角色个数失败!!");
            parserResult.setStatus(ParserResult.ERROR);
            parserResult.setMessage("查询角色个数失败！！");
            return parserResult;
        }
        logger.info("查询角色个数成功！" + count);

        List<Map> list = new ArrayList<>();
        int sum = count.intValue();
        for (int i = 0; i < sum; i++) {
            String name = null;
            try {
                name = user.getRoleNameByIndex(new BigInteger(i + "")).send();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("未获取到第" + i + "个角色名称");
                continue;
            }
            Tuple3<List<BigInteger>, String, String> tuple3;
            try {
                tuple3 = user.getRoleInfo(name).send();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("获取角色名称:" + name + "失败！！");
                continue;
            }
            Map result = new HashMap();
            List<BigInteger> list1 = tuple3.getValue1();
            String a = "";
            for (BigInteger bigInteger : list1) {
                a = a + bigInteger.toString() + "/    ";
            }
            logger.info(a);
            result.put("roleId", tuple3.getValue1().toString());
            result.put("roleFName", tuple3.getValue2());
            result.put("roleName", tuple3.getValue3());

            list.add(result);
        }
        logger.info("获取角色信息成功!" + list.toString());
        parserResult.setMessage("获取全部角色信息成功！");
        parserResult.setStatus(ParserResult.SUCCESS);
        parserResult.setData(list);
        return parserResult;
    }


    /**
     * 改变角色的权限和上级角色 角色权限现在只能增加，不能减少
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult changeRolePowerAndFName(Map map) {
        ParserResult parserResult = new ParserResult();
        logger.info("开始修改角色信息！修改信息：" + map.toString());
        User user = contractUtil.UserLoad();
        if (ethereumUtil.UnlockAccount()) {
            try {
                user.changeRoleIdAndFNameId(map.get("roleName").toString(), new BigInteger(map.get("roleId").toString()), map.get("fName").toString()).send();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("修改角色信息失败！！" + map);
                parserResult.setStatus(ParserResult.ERROR);
                parserResult.setMessage("error");
            }
        }else{
            logger.error("管理员解锁失败");
            parserResult.setStatus(ParserResult.ERROR);
            parserResult.setMessage("管理员解锁失败");
        }
        logger.info("修改角色信息成功！！");
        parserResult.setMessage("success");
        parserResult.setStatus(ParserResult.SUCCESS);
        return parserResult;
    }
}
