package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskListWhiteDao;
import com.tiefan.frc.dqs.domain.RiskListWhite;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
public class RiskListWhiteDaoImpl extends BaseDaoImpl implements IRiskListWhiteDao {

    @Override
    public RiskListWhite selectByAccount(String account) {
        return findObjByParameter(NAMESPACE + "selectByAccount", account, RiskListWhite.class);
    }

    @Override
    public List<RiskListWhite> whiteList(List<Map> list) {
        return findAll(NAMESPACE + "whiteList", list, RiskListWhite.class);
    }
}
