package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IXhyRiskItemValueDetailDao;
import com.tiefan.frc.dqs.domain.ItemValue;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wzq on 2019/7/6.
 */
@Repository
public class XhyRiskItemValueDetailDaoImpl extends BaseDaoImpl implements IXhyRiskItemValueDetailDao {

    @Override
    public Integer insert(List<ItemValue> itemValues){
        Map map =new HashMap();
        map.put("itemValues",itemValues);
        return update(NAMESPACE + "insert",map);
    }
}
