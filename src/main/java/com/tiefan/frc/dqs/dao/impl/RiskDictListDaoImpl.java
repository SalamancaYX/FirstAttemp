package com.tiefan.frc.dqs.dao.impl;

import java.util.List;

import com.tiefan.frc.dqs.dao.IRiskDictListDao;
import com.tiefan.frc.dqs.domain.RiskDictList;
import org.springframework.stereotype.Repository;

/**
 * @author wuzq
 */
@Repository
public class RiskDictListDaoImpl extends BaseDaoImpl implements IRiskDictListDao {
    /**
     * 命名空间
     */
    private static final String NAMESPACE = "com.tiefan.frc.dqs.dao.IRiskDictListDao.";

    @Override
    public List<RiskDictList> selectDictListAll() {
        return findAll(NAMESPACE + "selectDictListAll",RiskDictList.class);
    }

}
