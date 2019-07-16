package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskItemGroupFspDao;
import com.tiefan.frc.dqs.domain.RiskItemGroupFsp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wuzq
 */
@Repository
public class RiskItemGroupFspDaoImpl extends BaseDaoImpl implements IRiskItemGroupFspDao{

    @Override
    public List<RiskItemGroupFsp> selectAll() {
        return findAll(NAMESPACE + "selectAll",RiskItemGroupFsp.class);
    }
}
