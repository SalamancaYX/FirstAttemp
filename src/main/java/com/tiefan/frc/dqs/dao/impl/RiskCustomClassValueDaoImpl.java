package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskCustomClassValueDao;
import com.tiefan.frc.dqs.domain.RiskCustomClassValue;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author skybao
 */
@Repository
public class RiskCustomClassValueDaoImpl extends BaseDaoImpl implements IRiskCustomClassValueDao {
    private static final String NAMESPACE = "com.tiefan.frc.dqs.dao.IRiskCustomClassValueDao.";

    @Override
    public List<RiskCustomClassValue> queryAppChannels() {
        return getSst().selectList(NAMESPACE + "queryAppChannels");
    }
}
