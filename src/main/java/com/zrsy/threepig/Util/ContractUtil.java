package com.zrsy.threepig.Util;


import com.zrsy.threepig.Contract.PIG.Pig;
import com.zrsy.threepig.Contract.RBAC.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;

/**
 * 智能合约工具类，直接引入合约
 */
@Component
public class ContractUtil {

    @Value("${web3_url}")
    private String web3_url;
    @Value("${Pig_address}")
    private  String Pig_address;
    @Value("${User_address}")
    private String  User_address;
    @Value("${account_address}")
    private String account_address;

    public Pig PigLoad(){
        Web3j web3j=Web3j.build(new HttpService(web3_url));
        TransactionManager clientTransactionManager=new ClientTransactionManager(web3j,account_address) ;
        ContractGasProvider contractGasProvider=new DefaultGasProvider();
        return  Pig.load(Pig_address,web3j,clientTransactionManager,contractGasProvider.getGasPrice(),contractGasProvider.getGasLimit());
    }

    public Pig PigLoad(String accountAddress){
        Web3j web3j=Web3j.build(new HttpService(web3_url));
        TransactionManager clientTransactionManager=new ClientTransactionManager(web3j,accountAddress) ;
        ContractGasProvider contractGasProvider=new DefaultGasProvider();
        return  Pig.load(Pig_address,web3j,clientTransactionManager,contractGasProvider.getGasPrice(),contractGasProvider.getGasLimit());
    }

    public User UserLoad(){
        Web3j web3j=Web3j.build(new HttpService(web3_url));
        TransactionManager clientTransactionManager=new ClientTransactionManager(web3j,account_address) ;
        ContractGasProvider contractGasProvider=new DefaultGasProvider();
        return  User.load(User_address,web3j,clientTransactionManager,contractGasProvider.getGasPrice(),contractGasProvider.getGasLimit());
    }

    public User UserLoad(String accountaddress){
        Web3j web3j=Web3j.build(new HttpService(web3_url));
        TransactionManager clientTransactionManager=new ClientTransactionManager(web3j,accountaddress) ;
        ContractGasProvider contractGasProvider=new DefaultGasProvider();
        return  User.load(User_address,web3j,clientTransactionManager,contractGasProvider.getGasPrice(),contractGasProvider.getGasLimit());
    }

    public static void main(String[] args) throws IOException {
        Admin web3j= Admin.build(new HttpService("http://192.168.85.134:8545"));
        NewAccountIdentifier newAccountIdentifier=web3j.personalNewAccount("22345678").send();
        System.out.println(newAccountIdentifier.getAccountId());
    }
}