package com.zrsy.threepig.controller.android;

import com.zrsy.threepig.domain.ParserResult;
import com.zrsy.threepig.service.android.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
    private Logger logger= LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    TransactionService transactionService;

    /**
     * 查询地址余额
     * @param address
     * @return
     */
    @GetMapping("/getBalanceOf/{address}")
    public ParserResult getBanlanceOf(@PathVariable String address){
       logger.info("接收到查询地址为"+address+"的余额请求！");
       return transactionService.getBanlanceOf(address);
    }

    /**
     * 查询猪的信息
     * @param earId
     * @return
     */
    @GetMapping("/getPig/{earId}")
    public ParserResult getPigInfo(@PathVariable String earId){
        logger.info("接收到查询耳号为"+earId+"猪的信息的请求！！");
        return transactionService.getPigInfo(earId);
    }

    /**
     * 买家确认购买猪
     * @param address 买家地址
     * @param parameter 猪的721ID
     * @return
     */
    @GetMapping("/confirmBuy/{address}/{721ID}/{password}")
    public ParserResult confirmBuy(@PathVariable String address, @PathVariable("721ID") String parameter,@PathVariable String password){
        logger.info("接收到地址为"+address+"用户确认购买721ID为："+parameter+"猪的请求！！");
        return transactionService.confirmBuy(address,parameter,password);
    }

    @GetMapping("/preSale/{tokenId}/{address}/{password}")
    public ParserResult preSale(@PathVariable String tokenId, @PathVariable String address, @PathVariable String password){
        logger.info("接收到代售请求！！！");
        return transactionService.preSale(tokenId,address,password);
    }

    /**
     * 卖家确认发货
     * @param fromAddress 卖家地址
     * @param toAddress 买家地址
     * @param parameter 猪的721ID
     *
     * @return
     */
    @GetMapping("/transfer/{fromAddress}/{toAddress}/{721ID}/{password}")
    public ParserResult transfer(@PathVariable String fromAddress, @PathVariable String toAddress, @PathVariable("721ID") String parameter, @PathVariable String password){
        logger.info("接收到卖家确认发货的请求！！");
        return transactionService.transfer(fromAddress,toAddress,parameter,password);
    }

    /**
     * 买家确认收货
     * @param fromAddress 买家地址
     * @param toAddress 卖家地址
     * @param parameter 猪的721ID
     * @return
     */
    @GetMapping("/changeStatus/{fromAddress}/{toAddress}/{721ID}/{password}")
    public ParserResult changeStatus(@PathVariable String fromAddress, @PathVariable String toAddress, @PathVariable("721ID") String parameter, @PathVariable String password){
        logger.info("接收到买家确认收货的请求！！！");
        return transactionService.changeStatus(fromAddress,toAddress,parameter,password);
    }

    @GetMapping("/getAllPig/{address}")
    public ParserResult getAllPig(@PathVariable String address){
        logger.info("接收到获取合约中的全部猪的信息");
        return transactionService.getAllPig(address);
    }
}
