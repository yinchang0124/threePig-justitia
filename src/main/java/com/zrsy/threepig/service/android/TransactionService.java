package com.zrsy.threepig.service.android;

import com.sun.mail.imap.protocol.ID;
import com.zrsy.threepig.domain.ParserResult;
import org.springframework.web.bind.annotation.PathVariable;

public interface TransactionService {
    ParserResult getBanlanceOf(String address);

    ParserResult getPigInfo(String earId);

    ParserResult confirmBuy(String address, String id,String password);

    ParserResult transfer(String fromAddress,String toAddress,String id,String password);

    ParserResult changeStatus(String fromAddress,String toAddress,String id,String password);

    ParserResult preSale(String earId,String address,String password);

    ParserResult getAllPig(String address);
}
