package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IYuneeShowListDao;
import com.tiefan.frc.dqs.domain.YuneeShowList;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Repository
public class YuneeShowListImpl extends BaseDaoImpl implements IYuneeShowListDao {


    @Override
    public YuneeShowList select(int type, String value) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("value", value);
        return getSst().selectOne(NAMESPACE + "select", map);
    }

}
