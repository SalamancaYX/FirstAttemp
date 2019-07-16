package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IXhyUniformCreditQuotaActivationDao;
import com.tiefan.frc.dqs.domain.XhyUniformCreditQuotaActivation;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class XhyUniformCreditQuotaActivationDaoImpl extends BaseDaoImpl implements IXhyUniformCreditQuotaActivationDao{



    @Override
    public XhyUniformCreditQuotaActivation queryByMemberId(String memberId) {
        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        return getSst().selectOne(NAMESPACE + "queryByMemberId", map);
    }

}
