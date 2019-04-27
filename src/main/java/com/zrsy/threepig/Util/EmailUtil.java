package com.zrsy.threepig.Util;

import com.zrsy.threepig.BigchainDB.KeyPairHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * 邮件的工具类
 */
@Component("emailtool")
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${sendEmailaccount}")
    private String emailAccount;

    /**
     * 给指定邮箱发送邮件
     * @param emailAddress 邮箱账号
     * @param address 用户地址
     */
    public boolean sendSimpleMail(String emailAddress,String address){
        String ekeyPath=getKeyPath(address);
        MimeMessage message = null;
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailAccount);
            helper.setTo(emailAddress);
            helper.setSubject("智能养猪管理系统审核通知！！！！");

            StringBuffer sb = new StringBuffer();
            sb.append("<h1>恭喜您在智能养猪管理系统注册成功！！！</h1>")
                    .append("<p >附件中的两个密钥文件请妥善保管，不要丢失泄露！！</p>")
                    .append("<p >登录密钥文件用于登录和交易使用</p>")
                    .append("<p >数据密钥文件用于系统内设备使用</p>");
            helper.setText(sb.toString(), true);
            FileSystemResource fileSystemResource=new FileSystemResource(new File(ekeyPath));
            FileSystemResource fileSystemResource1=new FileSystemResource(new File("./keystore/"+address+"/keypair.txt"));
            helper.addAttachment("登录密钥",fileSystemResource);
            helper.addAttachment("数据密钥",fileSystemResource1);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 获得相应地址的秘钥文件路径
     * @param address 地址
     * @return
     */
    private String getKeyPath(String address){
        File file = new File("./keystore/"+address);
        File[] files=file.listFiles();
        String fileName=files[0].getName();
        KeyPairHolder.SaveKeyPairToTXT(KeyPairHolder.getKeyPair(),"./keystore/"+address+"/keypair.txt");
        return "./keystore/"+address+"/"+fileName;
    }

}

