package com.zrsy.threepig.domain.BDQL;

import com.alibaba.fastjson.JSONObject;
import com.bigchaindb.model.Asset;
import com.bigchaindb.model.Assets;
import com.bigchaindb.model.Transaction;
import com.google.gson.internal.LinkedTreeMap;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;


import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 表结构
 */
public class Table {
    private String tableName;
    private String type;
    private List<String> columnName;
    private List<Map> data;
    private Map<String, String> rowData;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getColumnName() {
        return columnName;
    }

    public void setColumnName(List<String> columnName) {
        this.columnName = columnName;
    }

    /**
     * transactions 集合必须是表名相同
     *
     * @param transaction
     */
    public void setColumnName(Transaction transaction) {
        List<String> result = new ArrayList<String>();
        if (!(this.tableName.equals(null) && this.type.equals(null))) {
            if (this.type.equals("CREATE")) {
                Map map = (Map) transaction.getAsset().getData();
                if (map.get("tableName").toString().equals(this.tableName)) {
                    map = (LinkedTreeMap) map.get("tableData");
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        result.add(key);
                    }
                }

            } else {
                Map map = (Map) transaction.getMetaData();
                if (map.get("tableName").toString().equals(this.tableName)) {
                    map = (LinkedTreeMap) map.get("tableData");
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        result.add(key);
                    }
                }
            }
            result.add("TXID");
            HashSet h = new HashSet(result);
            result.clear();
            result.addAll(h);
            this.columnName = result;

        } else {
            this.columnName = null;
        }
    }

    /**
     * 通过asserts获得表数据和表头（列名）
     *
     * @param assets
     */
    public void setTableDataWithColumnName(Assets assets) {
        List<String> result = new ArrayList<String>();
        if (!(this.tableName.equals(null) && this.type.equals(null))) {
            for (Asset asset : assets.getAssets()) {
                Map map = (Map) asset.getData();
                map = (LinkedTreeMap) map.get("tableData");
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    result.add(key);
                }
                result.add("TXID");
                map.put("TXID", asset.getId());
                List<Map> list = new ArrayList<Map>();
                list.add(map);
                if (this.data == null) {
                    this.data = list;
                } else {
                    list.addAll(this.data);
                    this.data = list;
                }
            }
            HashSet h = new HashSet(result);
            result.clear();
            result.addAll(h);
            this.columnName = result;
        } else {
            this.columnName = null;
        }
    }

    /**
     * 根据assets设置表数据
     *
     * @param assets
     */
    public void setTableData(Assets assets) {
        List<Map> data = new LinkedList<>();
        if (!(this.tableName.equals(null) && this.type.equals(null) && this.columnName.equals(null))) {
            for (Asset asset : assets.getAssets()) {
                Map map = (Map) asset.getData();
                map = (LinkedTreeMap) map.get("tableData");
                Map map1 = new HashMap();
                for (String name : this.columnName) {
                    if (name.equals("TXID")) {
                        map1.put(name, asset.getId());
                    } else {
                        map1.put(name, map.get(name));
                    }
                }

                data.add(map1);
            }
        }
        this.data = data;
    }

    /**
     * 根据metadatas设置表数据
     *
     * @param metaDatas
     */
    public void setTableData(List<MetaData> metaDatas) {
        List<Map> data = new LinkedList<>();
        if (!(this.tableName.equals(null) && this.type.equals(null) && this.columnName.equals(null))) {
            for (MetaData metaData : metaDatas) {
                Map map =  metaData.getMetadata();
                map = (Map) map.get("tableData");
                Map map1 = new HashMap();
                for (String name : this.columnName) {
                    if (name.equals("TXID")) {
                        map1.put(name, metaData.getId());
                    } else {
                        map1.put(name, map.get(name));
                    }
                }
                data.add(map1);
            }
        }
        this.data = data;
    }

    /**
     * 根据metadatas设置表数据和表头（列名）
     *
     * @param metaDatas
     */
    public void setTableDataWithCloumnName(List<MetaData> metaDatas) {
        List<String> result = new ArrayList<String>();
        if (!(this.tableName.equals(null) && this.type.equals(null))) {
            for (MetaData metadata : metaDatas) {
                Map map =  metadata.getMetadata();
                Map map1 = (JSONObject) map.get("tableData");

                Set<String> keys = map1.keySet();
                for (String key : keys) {
                    result.add(key);
                }
                result.add("TXID");
                map1.put("TXID",metadata.getId());
                List<Map> list = new ArrayList<Map>();
                list.add(map1);
                if (this.data == null) {
                    this.data = list;
                } else {
                    list.addAll(this.data);
                    this.data = list;
                }
            }
            HashSet h = new HashSet(result);
            result.clear();
            result.addAll(h);
            this.columnName = result;
        } else {
            this.columnName = null;
        }

    }


    private void setTablesWhere(Expression expression) {
        List<Map> newList = new ArrayList<>();
        if (expression instanceof EqualsTo) {
            String left = ((EqualsTo) expression).getLeftExpression().toString();
            String right = ((EqualsTo) expression).getRightExpression().toString();
            for (Map map : this.data) {
                if (map.get(left).toString().equals(right)) {
                    newList.add(map);

                }
            }
            this.data = newList;
        }
        if (expression instanceof GreaterThan) {
            String left = ((GreaterThan) expression).getLeftExpression().toString();
            String right = ((GreaterThan) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (Map map : this.data) {
                if (Integer.parseInt(map.get(left).toString()) > R) {
                    newList.add(map);
                }
            }
            this.data = newList;

        }
        if (expression instanceof GreaterThanEquals) {
            String left = ((GreaterThanEquals) expression).getLeftExpression().toString();
            String right = ((GreaterThanEquals) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (Map map : this.data) {
                if (Integer.parseInt(map.get(left).toString()) >= R) {
                    newList.add(map);
                }
            }
            this.data = newList;
        }
        if (expression instanceof MinorThan) {
            String left = ((MinorThan) expression).getLeftExpression().toString();
            String right = ((MinorThan) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (Map map : this.data) {
                if (Integer.parseInt(map.get(left).toString()) < R) {
                    newList.add(map);
                }
            }
            this.data = newList;
        }
        if (expression instanceof MinorThanEquals) {
            String left = ((MinorThanEquals) expression).getLeftExpression().toString();
            String right = ((MinorThanEquals) expression).getRightExpression().toString();
            int R = Integer.parseInt(right);
            for (Map map : this.data) {
                if (Integer.parseInt(map.get(left).toString()) <= R) {
                    newList.add(map);
                }
            }
            this.data = newList;
        }

    }


    public List<Map> getData() {
        return data;
    }

    public void setData(List<Map> data) {
        this.data = data;
    }


    public Map<String, String> getRowData() {
        return rowData;
    }

    public void setRowData(Map<String, String> rowData) {
        this.rowData = rowData;

    }

    public void setRowData(Transaction transaction) {
        LinkedTreeMap map = new LinkedTreeMap();
        if (transaction.getOperation().equals("\"CREATE\"")) {
            map = (LinkedTreeMap) transaction.getAsset().getData();
            map = (LinkedTreeMap) map.get("tableData");
        } else {
            map = (LinkedTreeMap) transaction.getMetaData();
            map = (LinkedTreeMap) map.get("tableData");
        }
        List<Map> list = new ArrayList<Map>();
        list.add(map);
        if (this.data == null) {
            this.data = list;
        } else {
            list.addAll(this.data);
            this.data = list;
        }


    }

    private static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public String toString() {
        return "{tableName:" + tableName + ",type:" + type + ",columnName:" + columnName.toString() + ",data:" + data.toString() + "}";
    }

    public static void main(String[] args) throws IOException {
        String a = "dfasdf";
        int b = Integer.parseInt(a);
        System.out.println(b);
    }
}
