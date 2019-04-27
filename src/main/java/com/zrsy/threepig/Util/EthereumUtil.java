package com.zrsy.threepig.Util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.JsonRpc2_0Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 针对geth的personal接口的工具类
 */
@Component
public class EthereumUtil {
    @Value("${web3_url}")
    private String web3_url;

    @Value("${account_address}")
    private String from;

    private Logger logger= LoggerFactory.getLogger(EthereumUtil.class);
    /**
     * 创建新用户
     *
     * @param password 密码
     * @return address
     */
    public String createNewAccount(String password) {
        Admin web3j = Admin.build(new HttpService(web3_url));
        NewAccountIdentifier newAccountIdentifier = null;
        try {
            newAccountIdentifier = web3j.personalNewAccount(password).send();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return newAccountIdentifier.getAccountId();
    }

    /**
     * 获得系统内的所有用户地址
     * @return
     */
    public List<String> getAllAccount() {
        Admin web3j = Admin.build(new HttpService(web3_url));
        PersonalListAccounts listAccounts = null;
        try {
            listAccounts = web3j.personalListAccounts().send();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return listAccounts.getAccountIds();
    }

    /**
     * 解锁账户
     * @param address
     * @param password
     * @return
     */
    public Boolean UnlockAccount(String address,String password){
        Admin web3j = Admin.build(new HttpService(web3_url));
        PersonalUnlockAccount personalUnlockAccount=null;
        try {
            personalUnlockAccount=web3j.personalUnlockAccount(address,password).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(personalUnlockAccount.accountUnlocked()==null){
            return true;
        }
        return personalUnlockAccount.accountUnlocked();
    }

    /**
     * 解锁管理员账户
     * @return
     */
    public Boolean UnlockAccount(){
        Admin web3j = Admin.build(new HttpService(web3_url));
        PersonalUnlockAccount personalUnlockAccount=null;
        try {
            personalUnlockAccount=web3j.personalUnlockAccount(from,"11111111").send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(personalUnlockAccount.accountUnlocked()==null){
            return true;
        }
        return personalUnlockAccount.accountUnlocked();
    }
    /**
     * 新账户转账100eth
     * @param to
     */
    public boolean sendTransaction(String to){
        Admin web3j = new JsonRpc2_0Admin(new HttpService(web3_url));
        UnlockAccount(from,"11111111");
        BigInteger value = Convert.toWei("10.0", Convert.Unit.ETHER).toBigInteger();
        Transaction transaction=  Transaction.createEtherTransaction(from,Transaction.DEFAULT_GAS,Transaction.DEFAULT_GAS ,Transaction.DEFAULT_GAS,to,value);
        try {
            EthSendTransaction ethSendTransaction=web3j.personalSendTransaction(transaction,"11111111").send();
            String hash=ethSendTransaction.getTransactionHash();
            if(!hash.equals(null)){
                return true;
            }
            else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取地址的余额
     * @param address
     * @return
     */
    public String  getBlance(String address){
        Web3j web3j=new JsonRpc2_0Web3j(new HttpService(web3_url));
        try {
            EthGetBalance ethGetBalance=web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            BigInteger balance=ethGetBalance.getBalance();

            balance=balance.divide(new BigInteger("1000000000000000000"));
            logger.info("获取地址为："+address+"的余额成功，余额为："+balance);
            return balance.toString();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取地址为"+address+"的余额失败！");
            return null;
        }
    }

    /**
     * 停止挖矿
     * @return
     */
    public boolean minerStop(){
        Map map=new HashMap<>();
        map.put("jsonrpc","2.0");
        map.put("method","miner_stop");
        map.put("params",new ArrayList<>());
        map.put("id",74);
        String json= JSON.toJSONString(map);
        String resp=HttpUtil.httpPost(web3_url,json);
        Map map1= (Map) JSON.parse(resp);
        return (boolean) map1.get("result");
    }

    /**
     * 开始挖矿
     * @return
     */
    public boolean minerStart(){
        List list=new ArrayList();
        list.add(1);
        Map map=new HashMap<>();
        map.put("jsonrpc","2.0");
        map.put("method","miner_start");
        map.put("params",list);
        map.put("id",74);
        String json= JSON.toJSONString(map);
        String resp=HttpUtil.httpPost(web3_url,json);
        Map map1= (Map) JSON.parse(resp);
        if(!map1.containsValue("result")){
            return true;
        }
        else{
            return false;
        }
    }

    public static void main(String[] args) throws IOException, CipherException {
        Credentials credentials = WalletUtils.loadCredentials("12345678", "keystore/0x4f35ae6c01aff6b750c1ff6a0404e40a348ca6dd/UTC--2019-03-26T08-59-55.561386305Z--4f35ae6c01aff6b750c1ff6a0404e40a348ca6dd");
        System.out.println(credentials.getAddress());
    }

}
