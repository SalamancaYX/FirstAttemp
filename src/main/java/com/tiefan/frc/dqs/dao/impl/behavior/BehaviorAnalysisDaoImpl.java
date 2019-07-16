package com.tiefan.frc.dqs.dao.impl.behavior;

import com.tiefan.frc.dqs.dao.behavior.IBehaviorAnalysisDao;
import com.tiefan.frc.dqs.domain.bas.BehaviorAnalysisDto;
import com.tiefan.frc.dqs.dto.behavior.BehaviorBaseData;
import com.tiefan.frc.dqs.support.constants.DqsConstant;
import com.tiefan.frc.dqs.support.utils.AesECBUtil;
import com.tiefan.frc.dqs.support.utils.DateTimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;


/**
 * BehaviorAnalysisDaoImpl
 * @author skybao
 */
@Repository
public class BehaviorAnalysisDaoImpl implements IBehaviorAnalysisDao {
    @Value(value = "${db_aesKey}")
    private String aesKey;
    @Resource(name = "sqlSessionTemplateBas")
    private SqlSessionTemplate basTemplate;

    @Override
    public List<BehaviorBaseData> queryLoginData(String mobile) {
        List<BehaviorBaseData> resList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(2);
        map.put("mobile", AesECBUtil.encrypt(mobile, aesKey));
        Calendar now = Calendar.getInstance();
        map.put("suffix", DateTimeUtil.getBehaviorSuffix(now.getTime()));
        resList.addAll(basTemplate.selectList(NAMESPACE + "queryLoginData", map));
        now.add(Calendar.MONTH, -1);
        //now减了一个月，还在6月之后
        if (now.after(DqsConstant.LINE)) {
            map.put("suffix", DateTimeUtil.getBehaviorSuffix(now.getTime()));
            resList.addAll(basTemplate.selectList(NAMESPACE + "queryLoginData", map));
        } else {
            //now减了一个月，在5月
            map.put("suffix", StringUtils.EMPTY);
            resList.addAll(basTemplate.selectList(NAMESPACE + "queryLoginData", map));
        }
        return resList;
    }

    @Override
    public BehaviorAnalysisDto queryCreditData(String memberId) {
        List<BehaviorAnalysisDto> resList = null;
        Map<String, Object> map = new HashMap<>(2);
        map.put("memberId", memberId);
        Calendar now = Calendar.getInstance();
        map.put("suffix", DateTimeUtil.getBehaviorSuffix(now.getTime()));
        resList = basTemplate.selectList(NAMESPACE + "queryCreditData", map);
        if (CollectionUtils.isNotEmpty(resList)){
            return resList.get(0);
        }
        now.add(Calendar.MONTH, -1);
        //now减了一个月，还在6月之后
        if (now.after(DqsConstant.LINE)) {
            map.put("suffix", DateTimeUtil.getBehaviorSuffix(now.getTime()));
            resList = basTemplate.selectList(NAMESPACE + "queryCreditData", map);
        } else {
            //now减了一个月，在5月
            map.put("suffix", StringUtils.EMPTY);
            resList = basTemplate.selectList(NAMESPACE + "queryCreditData", map);
        }
        if (CollectionUtils.isNotEmpty(resList)){
            return resList.get(0);
        }
        return null;
    }

    @Override
    public BehaviorAnalysisDto queryLastOneTradeDataOfDnh(String memberId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("memberId", memberId);
        Calendar now = Calendar.getInstance();
        map.put("suffix", DateTimeUtil.getBehaviorSuffix(now.getTime()));
        return basTemplate.selectOne(NAMESPACE + "queryLastOneTradeDataOfDnh",map);
    }

}




