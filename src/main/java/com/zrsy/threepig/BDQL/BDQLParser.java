package com.zrsy.threepig.BDQL;

import com.bigchaindb.api.OutputsApi;

import com.bigchaindb.model.*;
import com.zrsy.threepig.BigchainDB.BigchainDBRunner;
import com.zrsy.threepig.BigchainDB.BigchainDBUtil;
import com.zrsy.threepig.BigchainDB.KeyPairHolder;
import com.zrsy.threepig.domain.BDQL.BigchainDBData;
import com.zrsy.threepig.domain.BDQL.MetaData;

import com.zrsy.threepig.domain.BDQL.Table;
import com.zrsy.threepig.domain.ParserResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class BDQLParser {
    private static Logger logger = LoggerFactory.getLogger(BDQLParser.class);

    /**
     * 根据不同的类型的BDQL进行不同的解析,根据分号区分语句个数
     *
     * @param BDQL
     * @param sort
     */
    public static ParserResult BDQLParser(String BDQL, int sort) {
        ParserResult result = new ParserResult();
        logger.info("开始解析BDQL：" + BDQL + "，##########################");
        switch (sort) {
            case BDQLUtil.ONE:
                result = BDQLParserONE(BDQL);
                break;
            case BDQLUtil.TWO:
                result = BDQLParserTWO(BDQL);
                break;
            default:
                //TODO
                logger.info("BDQL语句：" + BDQL + ",此类型语法尚未完善");
        }
        return result;
    }

    /**
     * 区分不同的语法 select insert，update
     *
     * @param BDQL
     * @return
     */
    public static ParserResult BDQLParserONE(String BDQL) {
        ParserResult result = new ParserResult();
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(BDQL);
        } catch (JSQLParserException e) {
            logger.error("BDQL语法错误原因：" + getJSQLParserExceptionCauseBy(e));
            e.printStackTrace();
        }
        if (statement instanceof Select) {
            return selectParser((Select) statement);
        } else if (statement instanceof Insert) {
            return insertParser((Insert) statement);
        } else if (statement instanceof Update) {
            return updateParser((Update) statement);
        }
        result.setStatus(ParserResult.ERROR);
        result.setMessage("此种语法还未推出！！！");
        result.setData(null);
        return result;
    }

    /**
     * 分号后有两个语句，现在只支持insert和update在创建资产的时候应用
     *
     * @param BDQL
     * @return
     */
    public static ParserResult BDQLParserTWO(String BDQL) {
        ParserResult result = new ParserResult();
        //TODO ParserTwo BDQL
        return result;
    }


    /**
     * 开始解析select语句
     *
     * @param select Select
     */
    private static ParserResult selectParser(Select select) {
        ParserResult result = new ParserResult();
        Table table = new Table();

        PlainSelect selectBody = (PlainSelect) select.getSelectBody();


        Expression expression = selectBody.getWhere();
        //获得表名
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableNames = tablesNamesFinder.getTableList(select);

        if (tableNames.size() != 1) {
            logger.warn("多表查询功能还未推出！！！！");
            result.setStatus(ParserResult.ERROR);
            result.setMessage("多表查询功能还未推出！！！！");
            result.setData(null);
            return result;
        } else {
            table.setTableName(tableNames.get(0));
            //获得列名
            ArrayList<String> columnNames = (ArrayList<String>) getColumnNames(selectBody);
            if (expression instanceof EqualsTo && ((EqualsTo) expression).getLeftExpression().toString().equals("ID")) {//存在交易ID
                String TXID = ((EqualsTo) expression).getRightExpression().toString();
                Transaction transaction = BigchainDBUtil.getTransactionByTXID(TXID);
                if (transaction.getOperation().equals("CREATE")) {
                    table.setType("CREATE");
                    Assets assets = new Assets();
                    Asset asset = transaction.getAsset();
                    assets.addAsset(asset);
                    table.setTableDataWithColumnName(assets);

                } else {
                    table.setType("TRANSFER");
                    List<MetaData> metaDatas = new LinkedList<MetaData>();
                    MetaData metaData = (MetaData) transaction.getMetaData();
                    metaDatas.add(metaData);
                    table.setTableDataWithCloumnName(metaDatas);
                }

            } else {
                if (columnNames.size() == 1 && columnNames.get(0).equals("*")) {
                    if (BigchainDBUtil.getAssetByKey(table.getTableName()).size() == 0) {
                        List<MetaData> metaDatas = BigchainDBUtil.getMetaDatasByKey(table.getTableName());
                        table.setType("TRANSFER");
                        List<MetaData> newMetadatas=selectMetadata(metaDatas,expression);
                        table.setTableDataWithCloumnName(newMetadatas);
                    } else {
                        Assets assets = BigchainDBUtil.getAssetByKey(table.getTableName());
                        table.setType("CREATE");
                        Assets newAssets=selectAssets(assets,expression);
                        table.setTableDataWithColumnName(newAssets);
                    }
                } else {
                    if (BigchainDBUtil.getAssetByKey(table.getTableName()).size() == 0) {
                        List<MetaData> metaDatas = BigchainDBUtil.getMetaDatasByKey(table.getTableName());
                        table.setType("TRANSFER");
                        table.setColumnName(columnNames);
                        List<MetaData> newMetadatas=selectMetadata(metaDatas,expression);
                        table.setTableData(newMetadatas);

                    } else {
                        Assets assets = BigchainDBUtil.getAssetByKey(table.getTableName());
                        table.setType("CREATE");
                        table.setColumnName(columnNames);
                        Assets newAssets=selectAssets(assets,expression);
                        table.setTableData(newAssets);

                    }
                }
            }

            result.setStatus(ParserResult.SUCCESS);
            result.setData(table);
            result.setMessage("select");
            return result;
        }


    }

    /**
     * 获得select的查询列名
     *
     * @param selectBody
     * @return
     */
    private static List<String> getColumnNames(SelectBody selectBody) {
        PlainSelect plainSelect = (PlainSelect) selectBody;
        //获得查询的列名
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        List<String> str_items = new ArrayList<String>();
        if (selectItems != null) {
            for (int i = 0; i < selectItems.size(); i++) {
                str_items.add(selectItems.get(i).toString());
            }
        }
        return str_items;
    }

    /**
     * 根据where挑选asset
     * @param assets
     * @param expression
     * @return
     */
    private static Assets selectAssets(Assets assets,Expression expression){
        if(expression==null){
            return assets;
        }
        Assets newAssets=new Assets();
        if (expression instanceof EqualsTo) {
            String left = ((EqualsTo) expression).getLeftExpression().toString();
            String right = ((EqualsTo) expression).getRightExpression().toString();
            for (Asset asset : assets.getAssets()) {
                Map map=(Map) asset.getData();
                Map map1=(Map) map.get("tableData");
                if(map1.get(left).toString().equals(right)){
                    newAssets.addAsset(asset);
                }
            }

        }
        if (expression instanceof GreaterThan) {
            String left = ((GreaterThan) expression).getLeftExpression().toString();
            String right = ((GreaterThan) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (Asset asset : assets.getAssets()) {
                Map map=(Map) asset.getData();
                Map map1=(Map) map.get("tableData");
                if(Integer.parseInt(map1.get(left).toString())>R){
                    newAssets.addAsset(asset);
                }
            }


        }
        if (expression instanceof GreaterThanEquals) {
            String left = ((GreaterThanEquals) expression).getLeftExpression().toString();
            String right = ((GreaterThanEquals) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (Asset asset : assets.getAssets()) {
                Map map=(Map) asset.getData();
                Map map1=(Map) map.get("tableData");
                if(Integer.parseInt(map1.get(left).toString())>=R){
                    newAssets.addAsset(asset);
                }
            }

        }
        if (expression instanceof MinorThan) {
            String left = ((MinorThan) expression).getLeftExpression().toString();
            String right = ((MinorThan) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (Asset asset : assets.getAssets()) {
                Map map=(Map) asset.getData();
                Map map1=(Map) map.get("tableData");
                if(Integer.parseInt(map1.get(left).toString())<R){
                    newAssets.addAsset(asset);
                }
            }

        }
        if (expression instanceof MinorThanEquals) {
            String left = ((MinorThanEquals) expression).getLeftExpression().toString();
            String right = ((MinorThanEquals) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (Asset asset : assets.getAssets()) {
                Map map=(Map) asset.getData();
                Map map1=(Map) map.get("tableData");
                if(Integer.parseInt(map1.get(left).toString())<=R){
                    newAssets.addAsset(asset);
                }
            }

        }
        return newAssets;

    }

    /**
     * 根据where挑选metadata
     * @param metaDataList
     * @param expression
     * @return
     */
    private static List<MetaData> selectMetadata(List<MetaData> metaDataList,Expression expression){
        if(expression==null){
            return metaDataList;
        }
        List<MetaData> newMetadata=new ArrayList<>();
        if (expression instanceof EqualsTo) {
            String left = ((EqualsTo) expression).getLeftExpression().toString();
            String right = ((EqualsTo) expression).getRightExpression().toString();
            for (MetaData metaData : metaDataList) {
                Map map= metaData.getMetadata();
                Map map1=(Map) map.get("tableData");
                if(map1.get(left).toString().equals(right)){
                    newMetadata.add(metaData);
                }
            }

        }
        if (expression instanceof GreaterThan) {
            String left = ((GreaterThan) expression).getLeftExpression().toString();
            String right = ((GreaterThan) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (MetaData metaData : metaDataList) {
                Map map= metaData.getMetadata();
                Map map1=(Map) map.get("tableData");
                if(Integer.parseInt(map1.get(left).toString())>R){
                    newMetadata.add(metaData);
                }
            }


        }
        if (expression instanceof GreaterThanEquals) {
            String left = ((GreaterThanEquals) expression).getLeftExpression().toString();
            String right = ((GreaterThanEquals) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (MetaData metaData : metaDataList) {
                Map map= metaData.getMetadata();
                Map map1=(Map) map.get("tableData");
                if(Integer.parseInt(map1.get(left).toString())>=R){
                    newMetadata.add(metaData);
                }
            }

        }
        if (expression instanceof MinorThan) {
            String left = ((MinorThan) expression).getLeftExpression().toString();
            String right = ((MinorThan) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (MetaData metaData : metaDataList) {
                Map map= metaData.getMetadata();
                Map map1=(Map) map.get("tableData");
                if(Integer.parseInt(map1.get(left).toString())<R){
                    newMetadata.add(metaData);
                }
            }

        }
        if (expression instanceof MinorThanEquals) {
            String left = ((MinorThanEquals) expression).getLeftExpression().toString();
            String right = ((MinorThanEquals) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (MetaData metaData : metaDataList) {
                Map map= metaData.getMetadata();
                Map map1=(Map) map.get("tableData");
                if(Integer.parseInt(map1.get(left).toString())<=R){
                    newMetadata.add(metaData);
                }
            }

        }
        return newMetadata;

    }

    /**
     * 开始解析insert语句
     *
     * @param insert Insert
     */
    private static ParserResult insertParser(Insert insert) {
        ParserResult result = new ParserResult();
        //表名
        String tableName = insert.getTable().getName();
        //字段名
        List<Column> colums = insert.getColumns();
        //字段值
        ExpressionList expressionList = (ExpressionList) insert.getItemsList();
        List<Expression> values = expressionList.getExpressions();

        Map map = toMap(colums, values);
        BigchainDBData data = new BigchainDBData(tableName, map);

        String id = null;
        try {
            id = BigchainDBUtil.createAsset(data);
            logger.info("插入操作成功！！！！！");
        } catch (Exception e) {
            logger.error("插入操作失败！！！！！");
            result.setData(null);
            result.setMessage("插入操作失败！！！！！");
            e.printStackTrace();
        }
        result.setStatus(ParserResult.SUCCESS);
        result.setMessage("insert");
        result.setData(id);
        return result;
    }

    /**
     * 开始解析update语句
     *
     * @param update Update
     */
    private static ParserResult updateParser(Update update) {
        ParserResult result = new ParserResult();
        //获得where后的资产ID数据
        EqualsTo expression = (EqualsTo) update.getWhere();
        String ID = expression.getLeftExpression().toString();
        String values = expression.getRightExpression().toString();
        values = values.substring(1, values.length() - 1);

        if (!ID.equals("ID")) {
            logger.error("BDQL语法错误：where 只能使用ID=————，请检查书写和大小写");
            result.setMessage("BDQL语法错误：where 只能使用ID=————，请检查书写和大小写");
            result.setStatus(ParserResult.ERROR);
            result.setData(null);
            return result;
        }

        //表名
        String tableName = update.getTables().get(0).getName();

        //字段名
        List<Column> colums = update.getColumns();

        //字段值
        List<Expression> expressions = update.getExpressions();

        BigchainDBData data = new BigchainDBData(tableName, toMap(colums, expressions));
        String id = null;
        try {
            id = BigchainDBUtil.transferToSelf(data, values);
        } catch (Exception e) {
            logger.error("更新数据失败！！！！！！！");
            result.setStatus(ParserResult.ERROR);
            result.setData(null);
            result.setMessage("更新数据失败！！！！！！！");
            return result;
        }


        //TODO 插入完成后检查工作没有做

        if (id == null) {
            //TODO id为NULL
        } else {
            //TODO id不为null的时候，检查
        }
        result.setStatus(ParserResult.SUCCESS);
        result.setMessage("更新数据成功！！！");
        result.setData(id);

        logger.info("更新数据成功！！！！");
        logger.info("交易ID：" + id);
        return result;
    }


    /**
     * 开始解析insert和update，就是同时插入assert和metadat
     *
     * @param BDQL
     * @return
     */
    private static ParserResult insertAndUpdateParser(String BDQL) {
        ParserResult result = new ParserResult();
        //TODO
        return result;
    }

    /**
     * 将两个list 合并到一个map中
     *
     * @param key
     * @param value
     * @return
     */
    private static Map toMap(List key, List value) {
        Map<String, String> map = new HashMap();
        for (int i = 0; i < key.size(); i++) {
            String sk = BDQLUtil.fixString(key.get(i).toString());
            String sv = BDQLUtil.fixString(value.get(i).toString());
            map.put(sk, sv);
        }
        return map;
    }

    /**
     * 获得语法错误原因
     *
     * @param e JSQLParserException
     * @return string cause by
     */
    private static String getJSQLParserExceptionCauseBy(JSQLParserException e) {
        String[] ss = e.getCause().getMessage().split(":");
        String s = ss[1].replace("\n", "");
        String result = s.replace("Was expecting one of", "");
        logger.error("BDQL语法错误原因，正在查找……………………");
        return result;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ParserResult result = new ParserResult();
        BigchainDBRunner.StartConn();
        for (int j = 0; j < 10; j++) {
            result = BDQLUtil.work("INSERT INTO Computer (id, ip,mac,size,cpu,ROM,RAM) VALUES ('" + (j + 1) + "','" + (j + 2) + "','Champs-Elysees','" + (j + 3) + "','i7','" + (j + 4) + "','" + (j + 5) + "')");
            String id = (String) result.getData();
            logger.info("资产ID：" + id);

            logger.info(BigchainDBUtil.checkTransactionExit(id) + "");
            ParserResult result1 = new ParserResult();
            for (int i = 0; i < 10; i++) {
                result1 = BDQLUtil.work("UPDATE Person SET FirstName = '" + i + "' , SecondName='" + j + "',age= '" + (i + j) + "',time='" + (i + j + 10) + "' WHERE ID='" + id + "'");
                logger.info("交易ID：" + result1.getData());
                Thread.sleep(2000);

            }
        }

        Outputs outputs = OutputsApi.getOutputs(KeyPairHolder.pubKeyToString(KeyPairHolder.getPublic()));
        logger.info("交易总数1：" + outputs.getOutput().size());
        for (Output output : outputs.getOutput()) {
            logger.info("交易ID：" + output.getTransactionId() + ",密钥：" + output.getPublicKeys());
        }
        result = BDQLUtil.work("Select * from Computer where cpu= i7");
        logger.info(result.getData().toString());

    }
}