package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRong360MemberBaseInfoDao;
import com.tiefan.frc.dqs.domain.Rong360MemberBaseInfo;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class Rong360MemberBaseInfoDaoImpl extends BaseDaoImpl implements IRong360MemberBaseInfoDao {


    @Override
    public Rong360MemberBaseInfo selectByMemberId(String memberId){
        return findObjByParameter(NAMESPACE + "selectByMemberId", memberId,Rong360MemberBaseInfo.class);
    }
}
