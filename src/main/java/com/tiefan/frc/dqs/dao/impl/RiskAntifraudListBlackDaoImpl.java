package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskAntifraudListBlackDao;
import com.tiefan.frc.dqs.domain.RiskAntifraudListBlack;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
public class RiskAntifraudListBlackDaoImpl extends BaseDaoImpl implements IRiskAntifraudListBlackDao {

    @Override
    public Boolean isInBlackList(List<Map> list) {
        return findObjByParameter(NAMESPACE + "isInBlackList", list, RiskAntifraudListBlack.class) != null;
    }

    @Override
    public RiskAntifraudListBlack queryByTypeAndValue(Integer type, String value) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("value", value);
        return getSst().selectOne(NAMESPACE + "queryByTypeAndValue", map);
    }

    @Override
    public List<RiskAntifraudListBlack> queryListByTypeAndValues(List<Map> list,int isMd5) {
        Map map=new HashMap();
        map.put("list",list);
        map.put("isMd5",isMd5);
        return findListByParameter(NAMESPACE + "queryListByTypeAndValues", map, RiskAntifraudListBlack.class);
    }
}
