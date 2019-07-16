package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskJyqMemberDao;
import com.tiefan.frc.dqs.domain.RiskJyqMember;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RiskJyqMemberDaoImpl extends BaseDaoImpl implements IRiskJyqMemberDao {
    @Override
    public RiskJyqMember selectByIdno(String idno) {
        return findObjByParameter(NAMESPACE + "selectByIdno", idno, RiskJyqMember.class);
    }
    @Override
    public RiskJyqMember selectByMobile(String mobile) {
        return findObjByParameter(NAMESPACE + "selectByMobile", mobile, RiskJyqMember.class);
    }
    @Override
    public RiskJyqMember selectByMd5(String md5){
        return findObjByParameter(NAMESPACE + "selectByMd5", md5, RiskJyqMember.class);
    }

    @Override
    public List<RiskJyqMember> blackList(List<Map> list,int isMd5) {
        Map map=new HashMap();
        map.put("list",list);
        map.put("isMd5",isMd5);
        return findAll(NAMESPACE + "blackList", map, RiskJyqMember.class);
    }
}
