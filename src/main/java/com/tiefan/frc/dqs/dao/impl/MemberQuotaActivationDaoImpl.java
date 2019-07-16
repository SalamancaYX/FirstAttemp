package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IMemberQuotaActivationDao;
import com.tiefan.frc.dqs.domain.MemberQuotaActivation;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiafeilu on 2018/6/12.
 */
@Repository
public class MemberQuotaActivationDaoImpl extends BaseDaoImpl implements IMemberQuotaActivationDao {

    private static final String NAMESPACE = "com.tiefan.frc.dqs.dao.IMemberQuotaActivationDao.";

    @Override
    public MemberQuotaActivation queryByMemberId(String memberId) {
        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        return getSst().selectOne(NAMESPACE + "queryByMemberId", map);
    }
}
