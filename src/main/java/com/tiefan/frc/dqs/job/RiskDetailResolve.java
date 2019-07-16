package com.tiefan.frc.dqs.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.tiefan.frc.dqs.dao.IXhyCardloanCreditQuotaDao;
import com.tiefan.frc.dqs.dao.IXhyRiskItemValueDetailDao;
import com.tiefan.frc.dqs.domain.ItemValue;
import com.tiefan.frc.dqs.domain.XhyCardloanCreditQuota;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * 定时处理xhy_cardloan_credit_quota表item_values字段 结构化到xhy_risk_item_value_detail
 */
@Component
public class RiskDetailResolve {
    private static final Logger LOGGER = LoggerFactory.getLogger(RiskDetailResolve.class);
    @Resource
    private IXhyCardloanCreditQuotaDao cardloanCreditQuotaDao;

    @Resource
    private IXhyRiskItemValueDetailDao xhyRiskItemValueDetailDao;
    @Resource(name = "cssMongoTemplate")
    private MongoTemplate mongoTemplate;

    @PostConstruct
    protected void resolve() {
        new Thread("RiskDetailResolveThread"){
            @Override
            public void run() {
                while (true) {
                    XhyCardloanCreditQuota xhyCardloanCreditQuota = null;
                    try {
                        //每隔半秒扫描一次
                        Thread.sleep(500);
                        xhyCardloanCreditQuota = cardloanCreditQuotaDao.queryResolve();
                        if (xhyCardloanCreditQuota == null) {
                            continue;
                        }
                        LOGGER.info("查询到待处理 {}，{}",xhyCardloanCreditQuota.getId(),xhyCardloanCreditQuota.getItemValue());
                        //1、将状态改为处理中
                        int success = cardloanCreditQuotaDao.updateResolve(xhyCardloanCreditQuota);
                        LOGGER.info("待处理修改处理中 {},{}",xhyCardloanCreditQuota.getId(),xhyCardloanCreditQuota.getItemValue());
                        if(success==1){
                            if(StringUtils.isNotBlank(xhyCardloanCreditQuota.getItemValue())) {
                                //2、从mongo中获取规则执行json路径
                                String json = getitemValueInfo(xhyCardloanCreditQuota.getItemValue());
                                LOGGER.info("查询到mongodb {},{}",xhyCardloanCreditQuota.getId(),json.length());
                                if (StringUtils.isNotBlank(json)) {
                                    JSONArray array = JSONObject.parseArray(json);
                                    List<ItemValue> list=new ArrayList();
                                    int order = 0;
                                    //3、json路径转为一条条规则
                                    build(xhyCardloanCreditQuota.getItemValue(),array,"",list,order);
                                    LOGGER.info("build结束 {},{}",xhyCardloanCreditQuota.getId(),list.size());
                                    //4、分批插入数据库
                                    save(list);
                                    LOGGER.info("save结束 {},{}",xhyCardloanCreditQuota.getId(),list.size());
                                }else{
                                    LOGGER.info("mongodb==null {}",xhyCardloanCreditQuota.getId());
                                }
                            }else{
                                LOGGER.info("getItemValue==null {}",xhyCardloanCreditQuota.getId());
                            }
                            //5、状态更新为成功
                            cardloanCreditQuotaDao.updateResolveSuccess(xhyCardloanCreditQuota);
                        }else{
                            LOGGER.info("待处理修改处理中失败 {}",xhyCardloanCreditQuota.getId());
                        }
                    }catch (Exception e){
                        LOGGER.error("解析规则json异常",e);
                        if(xhyCardloanCreditQuota!=null){
                            //5、状态更新为异常
                            cardloanCreditQuotaDao.updateResolveError(xhyCardloanCreditQuota);
                        }
                    }
                }
            }
        }.start();

    }

    private int build(String uuid ,JSONArray array,String cid,List list,Integer order) throws Exception{
        for(int i=0;i<array.size();i++){
            ItemValue itemValue = array.getObject(i, ItemValue.class);
            if(itemValue.getLeft().length()>255){
                itemValue.getLeft().substring(0,255);
            }
            if(itemValue.getRight().length()>255){
                itemValue.getRight().substring(0,255);
            }
            if(itemValue.getOperatorStr().length()>255){
                itemValue.getOperatorStr().substring(0,255);
            }
            itemValue.setOrder(++order);
            itemValue.setCid(cid);
            itemValue.setItemValue(uuid);
            if(null==itemValue.getRuleId()) {
                itemValue.setRuleId("");
            }
            if(null==itemValue.getCustomTag()) {
                itemValue.setCustomTag("");
            }
            list.add(itemValue);
            if(StringUtils.isNotBlank(itemValue.getFireRulesResult())&&6!=itemValue.getRuleType()){
                try {
                    JSONArray cArray = JSONObject.parseObject(itemValue.getFireRulesResult()).getJSONArray("itemValue");
                    order=build(uuid, cArray, itemValue.getRuleId(), list, order);
                }catch (Exception e ){
                    LOGGER.error("解析规则json异常",e);
                    throw e;
                }
            }
        }
        return order;
    }

    private void save(List<ItemValue> list) throws Exception{
        List<ItemValue> save=new ArrayList<>();
        int i=0;
        for(ItemValue itemValue:list){
            save.add(itemValue);
            i++;
            if(i%20==0){
                try {
                    xhyRiskItemValueDetailDao.insert(save);
                }catch (Exception e){
                    LOGGER.error("保存规则异常",e);
                    throw e;
                }
                save.clear();
            }
        }
        if(save.size()!=0){
            xhyRiskItemValueDetailDao.insert(save);
        }
    }
    private String getitemValueInfo(String uuid) throws Exception{
        boolean b = false;
        try {
            DBObject dbObject = new BasicDBObject();
            dbObject.put("uuid", uuid);
            DBObject fieldObject = new BasicDBObject();
            fieldObject.put("data", true);
            Query query = new BasicQuery(dbObject, fieldObject);
            query.limit(1);
            Map<String,String> map = mongoTemplate.findOne(query, Map.class, "xhy_result_item_value");
            if(map!=null){
                return map.get("data");
            }
        } catch (Exception e) {
            LOGGER.error("获取结果异常", e);
            throw e;
        }
        return "[]";
    }

}
