package com.zrsy.threepig.BigchainDB;

import com.alibaba.fastjson.JSON;
import com.bigchaindb.api.AssetsApi;
import com.bigchaindb.api.OutputsApi;
import com.bigchaindb.api.TransactionsApi;
import com.bigchaindb.builders.BigchainDbTransactionBuilder;
import com.bigchaindb.constants.BigchainDbApi;
import com.bigchaindb.constants.Operations;


import com.bigchaindb.model.*;
import com.bigchaindb.util.NetworkUtils;
import com.google.gson.JsonSyntaxException;


import com.zrsy.threepig.BigchainDB.KeyPairHolder;
import com.zrsy.threepig.Util.HttpUtil;
import com.zrsy.threepig.domain.BDQL.BigchainDBData;
import com.zrsy.threepig.domain.BDQL.MetaData;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BigchainDBUtil {
    private static Logger logger = LoggerFactory.getLogger(BigchainDBUtil.class);






    /**
     * 创建资产数据，没有metadata数据
     *
     * @param assetDate
     * @return
     * @throws Exception
     */
    public static String createAsset(BigchainDBData assetDate) throws Exception {
        return createAsset(assetDate, null);
    }

    /**
     * 创建资产和metadata
     *
     * @param assetWrapper
     * @param metadataWrapper
     * @return
     * @throws Exception
     */
    public static String createAsset(BigchainDBData assetWrapper, BigchainDBData metadataWrapper) throws Exception {

        Transaction createTransaction = BigchainDbTransactionBuilder
                .init()
                .operation(Operations.CREATE)
                .addAssets(assetWrapper, assetWrapper.getClass())
                .addMetaData(metadataWrapper)
                .buildAndSign(
                        KeyPairHolder.getPublic(),
                        KeyPairHolder.getPrivate())
                .sendTransaction();
        return createTransaction.getId();
    }




    /**
     * 给资产增加metadata信息
     * <p>
     * youself is KeyPairHolder.getKeyPair() representive
     *
     * @param metaData
     * @param assetId
     * @return
     * @throws Exception
     */
    public static String transferToSelf(BigchainDBData metaData, String assetId) {

        Transaction transferTransaction = null;
        try {
            transferTransaction = BigchainDbTransactionBuilder
                    .init()
                    .operation(Operations.TRANSFER)
                    .addAssets(assetId, String.class)
                    .addMetaData(metaData)
                    .addInput(null, transferToSelfFulFill(assetId), KeyPairHolder.getPublic())
                    .addOutput("1", KeyPairHolder.getPublic())
                    .buildAndSign(
                            KeyPairHolder.getPublic(),
                            KeyPairHolder.getPrivate())
                    .sendTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("资产ID：" + assetId + ",不存在!!!!!!!");
            return null;
        }
        return transferTransaction.getId();
    }


    /**
     * 通过资产id获取最后交易输出
     *
     * @param assetId
     * @return
     * @throws IOException
     */
    private static FulFill transferToSelfFulFill(String assetId) throws IOException, InterruptedException {
        final FulFill spendFrom = new FulFill();
        String transactionId = getLastTransactionId(assetId);
        spendFrom.setTransactionId(transactionId);
        spendFrom.setOutputIndex(0);
        return spendFrom;
    }

    /**
     * 通过资产id获取最后交易id
     *
     * @param assetId asset Id
     * @return last transaction id
     * @throws IOException
     */
    public static String getLastTransactionId(String assetId) throws IOException, InterruptedException {
        return getLastTransaction(assetId);
    }

    /**
     * 通过资产id获得最后交易信息
     *
     * @param assetId assetId
     * @return last transaction
     * @throws IOException
     */
    public static String  getLastTransaction(String assetId) throws IOException, InterruptedException {
//        Transactions transactions = TransactionsApi.getTransactionsByAssetId(assetId, Operations.TRANSFER);
//        List<Transaction> transfers=transactions.getTransactions();
        String json= HttpUtil.httpGet(BigChainDBGlobals.getBaseUrl() + BigchainDbApi.TRANSACTIONS + "/?asset_id=" + assetId + "&operation=TRANSFER",50);
        List<Transaction> transfers=JSON.parseArray(json,Transaction.class);

        if (transfers != null && transfers.size() > 0) {
            return transfers.get(transfers.size() - 1).getId();
        } else {
            return assetId;
        }
    }

    /**
     * 通过资产id得到transaction（create）
     *
     * @param assetId
     * @return
     * @throws IOException
     */
    public static Transaction getCreateTransaction(String assetId) throws IOException {
        try {
            Transactions apiTransactions = TransactionsApi.getTransactionsByAssetId(assetId, Operations.CREATE);

            List<Transaction> transactions = apiTransactions.getTransactions();
            if (transactions != null && transactions.size() == 1) {
                return transactions.get(0);
            } else {
                return null;
            }

        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * Transaction获取交易id
     *
     * @param transaction
     * @return
     */
    private static String getTransactionId(Transaction transaction) {
        String withQuotationId = transaction.getId();
        return withQuotationId.substring(1, withQuotationId.length() - 1);
//        return transaction.getId();
    }

    /**
     * 检查交易是否存在
     *
     * @param txID
     * @return
     */
    public  static boolean checkTransactionExit(String txID) {
        try {
            Thread.sleep(2000);
            Transaction transaction = TransactionsApi.getTransactionById(txID);
            Thread.sleep(2000);
            if (!transaction.getId().equals(null)) {
                logger.info("交易存在！！ID：" + txID);
                return true;
            } else {
                logger.info("交易不存在！！ID：" + txID);
                return false;
            }
        } catch (Exception e) {
            logger.error("未知错误！！！");
            e.printStackTrace();
            return false;

        }

    }

    /**
     * 通过公钥获得全部交易
     *
     * @param publicKey
     * @return
     * @throws IOException
     */
    public static Transactions getAllTransactionByPubKey(String publicKey) throws IOException {
        Transactions transactions = new Transactions();
        Outputs outputs = OutputsApi.getOutputs(publicKey);
        for (Output output : outputs.getOutput()) {
            String assetId = output.getTransactionId();
            Transaction transaction = TransactionsApi.getTransactionById(assetId);
            transactions.addTransaction(transaction);
        }
        return transactions;
    }

    /**
     * 通过key查询资产
     *
     * @param key
     * @return
     */
    public static Assets getAssetByKey(String key) {
        try {
            return AssetsApi.getAssets(key);
        } catch (IOException e) {
            logger.error("未知错误！！！！！！！");
            return null;
        }
    }

    /**
     * 通过Key查询metadata
     *
     * @param key
     * @return
     */
    public static List<MetaData> getMetaDatasByKey(String key) {
        logger.debug("getMetaData Call :" + key);
        Response response;
        String body = null;
        try {
            response = NetworkUtils.sendGetRequest(BigChainDBGlobals.getBaseUrl() + BigchainDbApi.METADATA + "/?search=" + key);
            body = response.body().string();
            response.close();
        } catch (Exception e) {
            logger.error("未知错误！！！！！！！");
            return null;
        }

        return  JSON.parseArray(body, MetaData.class);
    }

    public static Transaction getTransactionByTXID(String ID){
        logger.info("开始查询交易信息：TXID："+ID);
        try {
            logger.info("查询成功！！！！！！");
            return TransactionsApi.getTransactionById(ID);
        } catch (IOException e) {
            logger.error("交易不存在，TXID："+ID);
            return null;
        }
    }


    public static void main(String[] args) throws IOException {

    }
}