package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IMemberBaseInfoDao;
import com.tiefan.frc.dqs.domain.MemberBaseInfo;
import com.tiefan.frc.dqs.support.utils.AesECBUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
public class MemberBaseInfoDaoImpl extends BaseDaoImpl implements IMemberBaseInfoDao {

    @Value("${db_aesKey}")
    private String aesKey;



    @Override
    public int queryCountOfEmergencyContactMobile(String mobile) {
        Object count = getSst().selectOne(NAMESPACE + "queryCountOfEmergencyContactMobile", mobile);
        return count == null ? 0 : (int) count;
    }

    @Override
    public List<MemberBaseInfo> queryLoanMembersPage(String startTime, String endTime, int startRow, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("endTime", endTime);
        map.put("startTime", startTime);
        map.put("startRow", startRow);
        map.put("pageSize", pageSize);
        List<MemberBaseInfo> list = getSst().selectList(NAMESPACE + "queryLoanMember", map);
        if (CollectionUtils.isNotEmpty(list)) {
            for (MemberBaseInfo memberBaseInfo : list) {
                decrypt(memberBaseInfo);
            }
        }
        return list;
    }

    @Override
    public MemberBaseInfo selectNotDecryptByMemberId(String memberId) {
        return getSst().selectOne(NAMESPACE + "selectByMemberId", memberId);
    }

    @Override
    public MemberBaseInfo selectByAccount(String account) {
        MemberBaseInfo baseInfo = getSst().selectOne(NAMESPACE + "selectByAccount", AesECBUtil.encrypt(account, aesKey));
        if (baseInfo != null) {
            baseInfo.setAuthMobile(AesECBUtil.decrypt(baseInfo.getAuthMobile(), aesKey));
            baseInfo.setAccount(AesECBUtil.decrypt(baseInfo.getAccount(), aesKey));
            baseInfo.setEmergencyContactMobile(AesECBUtil.decrypt(baseInfo.getEmergencyContactMobile(), aesKey));
            baseInfo.setEmergencyContactMobile2nd(AesECBUtil.decrypt(baseInfo.getEmergencyContactMobile2nd(), aesKey));
            baseInfo.setCarNo(AesECBUtil.decrypt(baseInfo.getCarNo(), aesKey));
            baseInfo.setWorkUnitPhone(AesECBUtil.decrypt(baseInfo.getWorkUnitPhone(), aesKey));
            baseInfo.setUserName(baseInfo.getUserName());
            baseInfo.setEmergencyContactName(baseInfo.getEmergencyContactName());
            baseInfo.setEmergencyContactName2nd(baseInfo.getEmergencyContactName2nd());
            baseInfo.setMail(AesECBUtil.decrypt(baseInfo.getMail(), aesKey));
            baseInfo.setIdNo(AesECBUtil.decrypt(baseInfo.getIdNoSecret(), aesKey));
            baseInfo.setIdNoSecret(baseInfo.getIdNo());
        }
        return baseInfo;
    }

    private void decrypt(MemberBaseInfo memberBaseInfo) {
        memberBaseInfo.setAccount(AesECBUtil.decrypt(memberBaseInfo.getAccount(), aesKey));
        memberBaseInfo.setAuthMobile(AesECBUtil.decrypt(memberBaseInfo.getAuthMobile(), aesKey));
        memberBaseInfo.setEmergencyContactMobile2nd(AesECBUtil.decrypt(memberBaseInfo.getEmergencyContactMobile2nd(), aesKey));
        memberBaseInfo.setEmergencyContactMobile(AesECBUtil.decrypt(memberBaseInfo.getEmergencyContactMobile(), aesKey));
        memberBaseInfo.setIdNoSecret(AesECBUtil.decrypt(memberBaseInfo.getIdNoSecret(), aesKey));
        memberBaseInfo.setWorkUnitPhone(AesECBUtil.decrypt(memberBaseInfo.getWorkUnitPhone(), aesKey));
        memberBaseInfo.setCarNo(AesECBUtil.decrypt(memberBaseInfo.getCarNo(), aesKey));
        memberBaseInfo.setMail(AesECBUtil.decrypt(memberBaseInfo.getMail(), aesKey));
    }

    @Override
    public MemberBaseInfo selectByMemberId(String memberId) {
        MemberBaseInfo baseInfo = getSst().selectOne(NAMESPACE + "selectByMemberId", memberId);
        if (baseInfo != null) {
            baseInfo.setIdNo(AesECBUtil.decrypt(baseInfo.getIdNoSecret(), aesKey));
            baseInfo.setAccount(AesECBUtil.decrypt(baseInfo.getAccount(), aesKey));
            baseInfo.setAuthMobile(AesECBUtil.decrypt(baseInfo.getAuthMobile(), aesKey));
        }
        return baseInfo;
    }

    @Override
    public MemberBaseInfo selectByMemberId2(String memberId) {
        MemberBaseInfo baseInfo = getSst().selectOne(NAMESPACE + "selectByMemberId", memberId);
        if (baseInfo != null) {
            decrypt(baseInfo);
        }
        return baseInfo;
    }

    @Override
    public MemberBaseInfo selectBaseInfoById(Integer id) {
        MemberBaseInfo baseInfo = findObjById(NAMESPACE + "selectBaseInfoById", id, MemberBaseInfo.class);
        if (baseInfo != null) {
            baseInfo.setIdNo(AesECBUtil.decrypt(baseInfo.getIdNoSecret(), aesKey));
        }
        return baseInfo;
    }
}
