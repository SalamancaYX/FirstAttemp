package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskItemFspJsonpathDao;
import com.tiefan.frc.dqs.domain.RiskItemGroupFspJsonpath;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wuzq
 */
@Repository
public class RiskItemGroupFspJsonpathDaoImpl extends BaseDaoImpl implements IRiskItemFspJsonpathDao {

    @Override
    public List<RiskItemGroupFspJsonpath> selectAll() {
        return findAll(NAMESPACE + "selectAll",RiskItemGroupFspJsonpath.class);
    }
}
