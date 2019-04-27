package com.zrsy.threepig.Util;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 封装了post，get的http请求类
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

//    public static GenericCallback handleServerResponse() {
//        //define callback methods to verify response from BigchainDBServer
//        GenericCallback callback = new GenericCallback() {
//
//            @Override
//            public void transactionMalformed(Response response) {
//                logger.info("malformed - " + response.message());
//                onFailure();
//            }
//
//            @Override
//            public void pushedSuccessfully(Response response) {
//                try {
//                    logger.info("pushedSuccessfully - " + response.body().string());
//                    onSuccess(response);
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void otherError(Response response) {
//                logger.info(response.body().toString());
//                logger.info("otherError" + response.message());
//                onFailure();
//            }
//        };
//
//        return callback;
//    }

    private static void onSuccess(Response response) throws IOException {
        //TODO : Add your logic here with response from server
        logger.info("Success : 操作成功");
    }

    private static void onFailure() {
        //TODO : Add your logic here
        logger.info("Transaction failed");
    }


    public static String httpGet(String url,int a) {
        String result = null;
        OkHttpClient client = new OkHttpClient.Builder()
//                .retryOnConnectionFailure(true)
                .connectTimeout(a, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(a, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(a,TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 发起get请求
     *
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        return httpGet(url,20);
    }

    /**
     * 发送httppost请求
     *
     * @param url
     * @param data  提交的参数为key=value&key1=value1的形式
     * @return
     */
    public static String httpPost(String url, String data) {
        String result = null;
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/html;charset=utf-8"), data);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            Response response = httpClient.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    public static void main(String[] args) {

    }


}