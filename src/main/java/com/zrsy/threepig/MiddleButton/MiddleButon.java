package com.zrsy.threepig.MiddleButton;

import com.zrsy.threepig.BDQL.BDQLUtil;
import com.zrsy.threepig.BigchainDB.BigchainDBUtil;
import com.zrsy.threepig.Contract.PIG.Pig;
import com.zrsy.threepig.Util.ContractUtil;
import com.zrsy.threepig.domain.BDQL.Table;
import com.zrsy.threepig.domain.ParserResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.tuples.generated.Tuple6;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Component
public class MiddleButon {

    @Autowired
    ContractUtil contractUtil;


    public boolean work(String ID) throws Exception {
        Pig pig = contractUtil.PigLoad();
        ParserResult parserResult = BDQLUtil.work("select * from pigInfo where tokenId =" + ID);
        Table table = (Table) parserResult.getData();
        Map map =  table.getData().get(0);
        String earId = map.get("earId").toString();
        String TXID=map.get("TXID").toString();


        Tuple6<String, BigInteger, String, BigInteger, BigInteger, BigInteger> tuple6= pig.getPig(new BigInteger(ID)).send();
        String ETH_status=tuple6.getValue5().toString();

        ParserResult result=BDQLUtil.work("select * from pigStatus where earId="+earId);

        table = (Table) result.getData();
        int BD_status=0;
        for(Map map1:table.getData()){
             if(Integer.parseInt(map1.get("statu").toString())>BD_status){
                 BD_status=Integer.parseInt(map1.get("statu").toString());
             }
        }
        if((BD_status+"").equals(ETH_status)){
            return true;
        }else {
            return changeStatus(earId,ID,ETH_status,TXID);

        }
    }

    /**
     * 改变合约状态
     * @param earID
     * @param tokeID
     * @param EthStatus
     * @param asserID
     * @return
     * @throws InterruptedException
     */
    private boolean changeStatus(String earID,String tokeID,String EthStatus,String asserID) throws InterruptedException {
        ParserResult parserResult=BDQLUtil.work("update pigStatus set earID="+earID+",tokenId="+tokeID+",statu="+EthStatus+"where ID='"+asserID+"'");
        String TXID= (String) parserResult.getData();
        Thread.sleep(2000);
        if(BigchainDBUtil.checkTransactionExit(TXID)){
            return true;
        }else{
            return false;
        }
    }
}
