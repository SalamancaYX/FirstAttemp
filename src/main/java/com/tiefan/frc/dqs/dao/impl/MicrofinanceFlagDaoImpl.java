package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IMicrofinanceFlagDao;
import com.tiefan.frc.dqs.domain.MicrofinanceFlag;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skybao
 */
@Repository
public class MicrofinanceFlagDaoImpl extends BaseDaoImpl implements IMicrofinanceFlagDao {
    private static final String NAMESPACE = "com.tiefan.frc.dqs.dao.IMicrofinanceFlagDao.";

    @Override
    public List<MicrofinanceFlag> selectMemberIdPage(String startTime, String endTime, int startRow, int pageSize) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("startRow", startRow);
        map.put("pageSize", pageSize);
        return getSst().selectList(NAMESPACE + "selectMemberIdPage", map);
    }
}
