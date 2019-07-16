package com.tiefan.frc.dqs.service.impl.risk.channel.fsp;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.tiefan.frc.dqs.domain.RiskItemGroupFsp;
import com.tiefan.frc.dqs.domain.RiskItemGroupFspJsonpath;
import com.tiefan.frc.dqs.domain.RiskRuleItem;
import com.tiefan.frc.dqs.dto.ResponseVo;
import com.tiefan.frc.dqs.support.constants.RiskItemFieldTypeEnum;
import com.tiefan.frc.dqs.support.utils.JsonUtil;
import com.tiefan.frc.dqs.support.utils.http.FSPClientUtil;
import com.tiefan.frc.dqs.tool.DbCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.text.ExtendedMessageFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.PropertyPlaceholderHelper;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by georgewu on 2019/5/18.
 */
@Service
public class FspGroupItemInvokeServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspGroupItemInvokeServiceImpl.class);
    @Resource
    private FSPClientUtil fspUtil;


    public static String parseStringValue(String value, Map<String,String> map) {
        int startIndex = value.indexOf("{");
        while(startIndex != -1) {
            if(value.indexOf("}",startIndex)!=-1) {
                String key = value.substring(startIndex+1, value.indexOf("}", startIndex));
                if(StringUtils.isNotBlank(key)){
                    if(map!=null){
                        if(map.get(key)!=null){
                            value=value.replace("{"+key+"}",map.get(key));
                        }
                    }
                }
            }
            startIndex=value.indexOf("{",++startIndex);
        }

        return value;
    }



    public Map invokeFsp(Integer groupId, Map<String,String> pMap){
        Integer timeout=2;
        String url="";
        String parameter="";
        Map result = new HashMap();
        try {
            RiskItemGroupFsp fsp = DbCache.getInstance().getRiskItemGroupFspMap().get(groupId);
            if(fsp==null){
                LOGGER.error("调用"+groupId+"，数据库配置为空");
                return null;
            }
            if(fsp.getTimeOut()!=null){
                timeout=fsp.getTimeOut();
            }
            url = fsp.getFsp().trim();
            parameter = parseStringValue(fsp.getParam().trim(),pMap);
            LOGGER.info("调用{}，入参={}",url, parameter);
            String res = fspUtil.invokeTspService(url, parameter,timeout);
            ResponseVo responseVo = JsonUtil.toBean(res, ResponseVo.class);
            if (responseVo == null || !responseVo.isSuccess()) {
                LOGGER.warn("调用"+url+"接口异常,参数={}，返回={}", parameter, JsonUtil.toString(responseVo));
                return null;
            }
            JSONObject data = JsonUtil.toBean(responseVo.getData(),JSONObject.class);
            List<RiskItemGroupFspJsonpath> jsonpaths = DbCache.getInstance().getRiskItemGroupFspJsonpathMap().get(groupId);

            if(CollectionUtils.isEmpty(jsonpaths)){
                LOGGER.error("调用"+groupId+"，赋值对象为空");
                return null;
            }
            for(RiskItemGroupFspJsonpath jsonpath:jsonpaths){
                RiskRuleItem item = DbCache.getInstance().getRiskRuleItemsIdList().get(jsonpath.getItemId().intValue());
                String p = jsonpath.getProperty();
                Object v = getVal(data,jsonpath.getJsonPath(),item.getFieldType());
                LOGGER.info("调用"+groupId+"，对 {} 赋值 {}",p,v);
                result.put(p,v);
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("调用groupId："+groupId+" url:"+url+",接口异常,"+parameter,e);
            throw e;
        }
    }
    private Object getVal(JSONObject data,String path,int classType){
        try {
            Object value = JSONPath.eval(data, path);
            //字符串类型转double
            if(RiskItemFieldTypeEnum.isNum(classType)){
                if(value instanceof String){
                    value=Double.valueOf(value+"");
                }
            }
            //字符串类型转boolean
            if(RiskItemFieldTypeEnum.BOOLEAN.getCode()==classType){
                if(value instanceof String){
                    value=Boolean.valueOf(value+"");
                }
            }
            return value;
        }catch (Exception e){
            LOGGER.error("解析规则项失败："+data.toJSONString()+"："+path,e);
            return null;
        }
    }

}
