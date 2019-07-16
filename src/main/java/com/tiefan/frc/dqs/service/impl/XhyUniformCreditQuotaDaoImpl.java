package com.tiefan.frc.dqs.service.impl;

import com.tiefan.frc.dqs.dao.IXhyUniformCreditQuotaDao;
import com.tiefan.frc.dqs.domain.XhyUniformCreditQuota;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class XhyUniformCreditQuotaDaoImpl implements IXhyUniformCreditQuotaDao {
    private static final String NAMESPACE = "com.tiefan.frc.dqs.dao.IXhyUniformCreditQuotaDao.";

    @Resource(name = "sqlSessionTemplate")
    private SqlSessionTemplate sst;



    @Override
    public XhyUniformCreditQuota queryOneByMemberId(String memberId) {
        return sst.selectOne(NAMESPACE + "queryOneByMemberId", memberId);
    }


}
