package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IOverdueMobileDataDao;
import com.tiefan.frc.dqs.domain.OverdueMobileData;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OverdueMobileDataDaoImpl extends BaseDaoImpl implements IOverdueMobileDataDao {
    private static final String NAMESPACE = "com.tiefan.frc.dqs.dao.IOverdueMobileDataDao.";

    @Override
    public OverdueMobileData selectByMobile(Long mobile) {
        return getSst().selectOne(NAMESPACE + "selectByMobile", mobile);
    }

    @Override
    public int updateByMobile(OverdueMobileData overdueMobileData) {
        return getSst().update(NAMESPACE + "updateByMobile", overdueMobileData);
    }

    @Override
    public int insert(OverdueMobileData overdueMobileData) {
        return getSst().insert(NAMESPACE + "insert", overdueMobileData);
    }

    @Override
    public List<OverdueMobileData> selectAllByPage(int pageSize, int startRow, Date beginTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("pageSize", pageSize);
        params.put("startRow", startRow);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        return getSst().selectList(NAMESPACE + "selectAllByPage", params);
    }

    @Override
    public int insertBatch(List<OverdueMobileData> list) {
        return getSst().insert(NAMESPACE + "insertBatch", list);
    }
}
