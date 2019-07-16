package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IXhyCardloanCreditQuotaDao;
import com.tiefan.frc.dqs.domain.XhyCardloanCreditQuota;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiafeilu on 2019/4/22.
 */
@Repository
public class XhyCardloanCreditQuotaDaoImpl extends BaseDaoImpl implements IXhyCardloanCreditQuotaDao {

    @Override
    public XhyCardloanCreditQuota queryLatest(String memberId) {
        Map<String, Object>  map = new HashMap<>();
        map.put("memberId", memberId);
        return findObjByParameter(NAMESPACE + "queryLatest", map, XhyCardloanCreditQuota.class);
    }

    @Override
    public XhyCardloanCreditQuota queryResolve(){
        return findObj(NAMESPACE + "queryResolve",  XhyCardloanCreditQuota.class);
    }

    @Override
    public Integer updateResolve(XhyCardloanCreditQuota xhyCardloanCreditQuota){
        return update(NAMESPACE + "updateResolve",xhyCardloanCreditQuota);
    }
    @Override
    public Integer updateResolveSuccess(XhyCardloanCreditQuota xhyCardloanCreditQuota){
        return update(NAMESPACE + "updateResolveSuccess",xhyCardloanCreditQuota);
    }
    @Override
    public Integer updateResolveError(XhyCardloanCreditQuota xhyCardloanCreditQuota){
        return update(NAMESPACE + "updateResolveError",xhyCardloanCreditQuota);
    }
}
