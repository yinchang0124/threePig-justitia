package com.zrsy.threepig.Util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;

/**
 * 工具类
 * 文件操作
 */
@Component
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 创建json文件
     * 会覆盖之前的数据。
     *
     * @param info
     * @return
     */
    public boolean writeFile(String path, Map info) {

        //TODO 路径为测试路径，可更改
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("创建失败！！");
        }


        String json = JSON.toJSONString(info);
        FileWriter writer = null;
        try {
            writer = new FileWriter(path);
            writer.write("");
            writer.write(json);
            writer.flush();
            writer.close();
            logger.info("写入文件成功！！");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("写入文件失败！！");
            return false;
        }

        return true;
    }

    /**
     * 读取指定文件内容
     *
     * @param path 路径
     * @return
     */
    public String readFile(String path) {
        File file = new File(path);
        return readFile(file);

    }

    /**
     * 读取文件内容
     *
     * @param file
     * @return
     */
    public String readFile(File file) {
        if (file.isFile() && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String text = null;
                while ((text = bufferedReader.readLine()) != null) {
                    sb.append(text);
                }
                inputStreamReader.close();
                fileInputStream.close();
                return sb.toString();
            } catch (Exception e) {
                logger.info("文件读取失败");
                return null;
            }
        } else {
            logger.info("文件路径不存在！！！！");
            return null;
        }

    }

    /**
     * 创建文件夹
     *
     * @param dir 文件夹路径
     * @return true：创建成功和存在 false：创建失败
     */
    public boolean makedir(String dir) {
        File dirs = new File(dir);
        if (dirs.exists()) {
            logger.info("文件夹存在");
            return true;
        } else {
            if (dirs.mkdirs()) {
                logger.info("创建文件夹成功！！！");
                return true;
            } else {
                logger.error("创建文件夹失败！！！！");
                return false;
            }
        }

    }

    /**
     * 创建文件
     *
     * @param path 文件路径
     * @return 文件存在或者文件路径不存在返回 false  文件创建成功 返回true
     */
    public boolean createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
                logger.info("文件创建成功！！！");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("文件路径不存在！！");
                return false;
            }
        } else {
            logger.error("文件存在！！");
            return false;
        }
    }

    /**
     * 查看路径下的文件夹个数
     *
     * @param dir
     * @return 文件路径错误返回-1
     */
    public int getDirSize(String dir) {
        File file = new File(dir);
        if (file.exists()) {
            return file.listFiles().length;
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
//        Info info = new Info();
//        info.setDate("12.66");
//        info.setIp("192.168.1.3");
//        info.setId("0");
//        info.setType("tem");
//        writeFile(info);
        File file = new File("./JsonData/tem");
        File[] files = file.listFiles();

//        String ss=readFile("./JsonData/tem");
//        Info info1=  JSON.parseObject(ss,Info.class);
        logger.info(String.valueOf(files.length));
        logger.info(files[0].getName());
    }

}
