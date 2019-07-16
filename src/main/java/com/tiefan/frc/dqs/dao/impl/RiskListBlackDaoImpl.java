package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskListBlackDao;
import com.tiefan.frc.dqs.domain.RiskListBlack;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
public class RiskListBlackDaoImpl extends BaseDaoImpl implements IRiskListBlackDao {

    @Override
    public Boolean isInBlackList(List<Map> list) {
        return findObjByParameter(NAMESPACE + "isInBlackList", list, RiskListBlack.class) != null;
    }

    @Override
    public RiskListBlack select(int type, String value, int source) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("value", value);
        map.put("source", source);
        return getSst().selectOne(NAMESPACE + "select", map);
    }

    @Override
    public int insert(RiskListBlack record) {
        return update(NAMESPACE + "insert", record);
    }

    @Override
    public List<RiskListBlack> blackList(List<Map> list,int isMd5){
        Map map=new HashMap();
        map.put("list",list);
        map.put("isMd5",isMd5);
        return findAll(NAMESPACE + "blackList", map, RiskListBlack.class);
    }
}
