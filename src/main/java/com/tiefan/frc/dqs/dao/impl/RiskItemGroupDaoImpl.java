package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskDictListDao;
import com.tiefan.frc.dqs.dao.IRiskItemGroupDao;
import com.tiefan.frc.dqs.domain.RiskDictList;
import com.tiefan.frc.dqs.domain.RiskItemGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wuzq
 */
@Repository
public class RiskItemGroupDaoImpl extends BaseDaoImpl implements IRiskItemGroupDao{

    @Override
    public List<RiskItemGroup> selectAll() {
        return findAll(NAMESPACE + "selectAll",RiskItemGroup.class);
    }
}
