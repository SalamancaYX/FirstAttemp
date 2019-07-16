package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskListGrayDao;
import com.tiefan.frc.dqs.domain.RiskListGray;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
public class RiskListGrayDaoImpl extends BaseDaoImpl implements IRiskListGrayDao {

    @Override
    public List<RiskListGray> queryListByTypeAndValues(List<Map> list) {
        return findListByParameter(NAMESPACE + "queryListByTypeAndValues", list, RiskListGray.class);
    }

}
