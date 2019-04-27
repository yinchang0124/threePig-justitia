package com.zrsy.threepig.service.web.implement;

import com.zrsy.threepig.Contract.RBAC.User;
import com.zrsy.threepig.Util.*;
import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.web.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    protected static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${local_keypath}")
    private String local_keypath;

    @Value("${ssh_keypath}")
    private String ssh_keypath;

    @Autowired
    EmailUtil emailUtil;

    @Autowired
    EthereumUtil ethereumUtil;

    @Autowired
    SSHUtil sshUtil;

    @Autowired
    FileUtil fileUtil;

    @Autowired
    ContractUtil contractUtil;

    /**
     * 提交申请，1.在geth注册一个新用户，2.将geth中的秘钥复制到./keystore中 3.在智能合约中添加注册信息
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult register(Map map) {
        logger.info("用户开始注册！注册信息：" + map.toString());
        ParserResult parserResult = new ParserResult();
        String password = map.get("passwd").toString();
        String address = ethereumUtil.createNewAccount(password);
        logger.info("新用户地址：" + address);
        logger.info("开始获取秘钥");
        User user = contractUtil.UserLoad();
        User user1=contractUtil.UserLoad(address);
        if (downloadKeystore(address)) {
            try {
                ethereumUtil.UnlockAccount();
                logger.info("开始进行转账！！");
                BigInteger value = Convert.toWei("100.0", Convert.Unit.ETHER).toBigInteger();
                user.transfer(address,value).send();
                logger.info("对账户进行解锁！！");
                ethereumUtil.UnlockAccount(address, password);
                Thread.sleep(10000);
                TransactionReceipt transactionReceipt = user1.registerUser(map.get("username").toString(), map.get("email").toString()).send();
                List<User.NewRegisterUserEventResponse> responses = user.getNewRegisterUserEvents(transactionReceipt);
                if (responses.get(0).status.intValue() == 1) {
                    logger.info("注册成功!!");
                    parserResult.setStatus(ParserResult.SUCCESS);
                    parserResult.setMessage("success");
                    return parserResult;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger.info("注册失败！！");
        parserResult.setMessage("注册申请失败！！");
        parserResult.setStatus(ParserResult.ERROR);
        return parserResult;

    }

    /**
     * 下载远程服务器上的密钥文件到本地
     *
     * @param address 用户地址
     * @return
     */
    private boolean downloadKeystore(String address) {
        String path = address.substring(2, address.length());
        String sshPath;
        if (sshUtil.login()) {
            sshPath = "ls -l " + ssh_keypath + "*" + path;
            sshPath = sshUtil.execute(sshPath);
            String[] b = sshPath.split("/");
            sshPath = b[b.length - 1];
            sshPath = sshPath.substring(0, sshPath.length() - 1);
            sshPath = ssh_keypath + sshPath;
            String localPath = local_keypath + address;
            if (fileUtil.makedir(localPath)) {
                return sshUtil.copyFile(sshPath, localPath);
            } else {
                logger.error("本地创建文件路径失败，文件夹路径：" + localPath);
                return false;
            }
        } else {
            logger.error("连接远程服务器失败！！");
            return false;
        }

    }

    /**
     * 登录
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult login(Map map) {
        logger.info("开始进行登录！！");
        ParserResult parserResult = new ParserResult();
        logger.info("解锁账户！");
        if (ethereumUtil.UnlockAccount(map.get("address").toString(), map.get("password").toString())) {
            User user = contractUtil.UserLoad(map.get("address").toString());
            Tuple5<String, String, String, String, BigInteger> tuple5 = null;
            try {
                tuple5 = user.getUserInfo(map.get("account").toString()).send();
            } catch (Exception e) {
                logger.error("获取用户信息失败！！！");
                parserResult.setStatus(ParserResult.ERROR);
                parserResult.setMessage("fail");
                return parserResult;
            }
            logger.info("成功获取用户信息！！");
            String address = tuple5.getValue4();
            if (address.equals(map.get("address").toString())) {
                logger.info("信息比较成功！！！登陆成功！！");
                parserResult.setMessage("success");
                parserResult.setStatus(ParserResult.SUCCESS);
                return parserResult;
            } else {
                logger.error("信息不正确！！登录失败");
                parserResult.setStatus(ParserResult.ERROR);
                parserResult.setMessage("fail");
                return parserResult;
            }
        }
        logger.error("解锁账户失败！！");
        parserResult.setStatus(ParserResult.ERROR);
        parserResult.setMessage("fail");
        return parserResult;
    }


    /**
     * 获得全部用户信息
     *
     * @return
     */
    @Override
    public ParserResult getAllUser() {
        ParserResult parserResult = new ParserResult();
        User user = contractUtil.UserLoad();
        List<String> addressList = new ArrayList<>();
        if (ethereumUtil.UnlockAccount()) {
            try {
                addressList = user.getUserCount().send();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("获取全部用户信息失败！！！");
                parserResult.setStatus(ParserResult.ERROR);
                parserResult.setMessage("获取全部用户信息失败！！！");
                return parserResult;
            }
        } else {
            logger.error("解锁root账户失败！！！");
            parserResult.setStatus(ParserResult.ERROR);
            parserResult.setMessage("解锁root账户失败！！！");
            return parserResult;
        }

        logger.info("获取全部用户地址成功");
        List<Map> result = new ArrayList<>();
        for (String address : addressList) {
            Map map = new HashMap();
            Tuple6<String, String, String, String, String, BigInteger> tuple6 = null;
            try {
                tuple6 = user.getUserInfoByAddress(address).send();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("获取地址为：" + address + "的用户信息失败！！");
                continue;
            }
            map.put("address", address);
            map.put("userId", tuple6.getValue1());
            map.put("roleName", tuple6.getValue3());
            map.put("userFName", tuple6.getValue2());
            map.put("email", tuple6.getValue4());
            map.put("status", tuple6.getValue6().toString());
            result.add(map);
        }
        logger.info("获取用户信息完成！！");
        parserResult.setMessage("获取用户信息完成！！");
        parserResult.setStatus(ParserResult.SUCCESS);
        parserResult.setData(result);
        return parserResult;
    }

    /**
     * 提交用户申请
     *
     * @param map
     * @return
     */
    @Override
    public ParserResult eroll(Map map) {
        logger.info("开始提交用户申请");
        ParserResult parserResult = new ParserResult();
        User user = contractUtil.UserLoad();
        TransactionReceipt receipt = null;
        logger.info("开始解锁账户！");
        ethereumUtil.UnlockAccount();
        try {
            receipt = user.enroll(map.get("address").toString(), map.get("roleName").toString(), map.get("fName").toString()).send();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("提交注册申请失败！！信息：" + map.toString());
            parserResult.setMessage("提交申请失败！！！");
            parserResult.setStatus(ParserResult.ERROR);
            return parserResult;
        }
        List<User.UserChangeEventResponse> responses = user.getUserChangeEvents(receipt);

        if (responses.get(0).status.intValue() == 2) {
            logger.info("提交注册申请成功！！信息：" + map.toString());
            logger.info("开始给用户回复邮件");
            if (sendEmail(map.get("address").toString())) {
                logger.info("邮件发送成功！！");
                logger.info("注册提交成功！！！！");
                parserResult.setStatus(ParserResult.SUCCESS);
                parserResult.setMessage("success");
                return parserResult;
            } else {
                logger.info("邮件发送失败！！");
                logger.info("注册提交失败！！！！");
                parserResult.setStatus(ParserResult.SUCCESS);
                parserResult.setMessage("注册提交失败！！！！");
                return parserResult;
            }
        }
        logger.error("提交注册申请失败！！信息：" + map.toString());
        parserResult.setMessage("提交申请失败！！！");
        parserResult.setStatus(ParserResult.ERROR);
        return parserResult;
    }

    /**
     * 获取地址的邮箱，发送邮件
     *
     * @param address
     * @return
     */
    private boolean sendEmail(String address) {
        User user = contractUtil.UserLoad();
        Tuple6<String, String, String, String, String, BigInteger> tuple6 = null;
        try {
            tuple6 = user.getUserInfoByAddress(address).send();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取地址为：" + address + "的邮箱信息失败！！");
        }
        String email = tuple6.getValue4();
        logger.info("获取地址为：" + address + "的邮箱信息成功！！邮箱地址：" + email);
        logger.info("开始发送邮件！！");
        return emailUtil.sendSimpleMail(email, address);

    }

    /**
     * 禁用用户
     *
     * @param address
     * @return
     */
    @Override
    public ParserResult banUser(String address) {
        logger.info("开始禁用用户！！！禁用用户地址：" + address);
        ParserResult parserResult = new ParserResult();
        User user = contractUtil.UserLoad();
        logger.info("管理员解锁账户");
        ethereumUtil.UnlockAccount();
        TransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = user.deleteUser(address).send();
        } catch (Exception e) {
            e.printStackTrace();

        }
        List<User.UserChangeEventResponse> responses = user.getUserChangeEvents(transactionReceipt);
        if (responses.get(0).status.intValue() == 3) {
            logger.info("禁用用户成功！用户地址：" + address);
            parserResult.setMessage("success");
            parserResult.setStatus(ParserResult.SUCCESS);
            return parserResult;
        }
        logger.error("禁用用户失败！！用户地址：" + address);
        parserResult.setStatus(ParserResult.ERROR);
        parserResult.setMessage("error");
        return parserResult;
    }
}
