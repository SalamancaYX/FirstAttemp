/**
 *
 */
package com.tiefan.frc.dqs.service.impl;

import com.tiefan.frc.dqs.dao.IMemberBaseInfoDao;
import com.tiefan.frc.dqs.dao.IRong360MemberBaseInfoDao;
import com.tiefan.frc.dqs.domain.MemberBaseInfo;
import com.tiefan.frc.dqs.service.IMemberBaseInfoService;
import com.tiefan.frc.dqs.support.utils.AesECBUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author wangye3
 */
@Service
public class MemberBaseInfoServiceImpl implements IMemberBaseInfoService {
    private static final Logger logger = LoggerFactory.getLogger(MemberBaseInfoServiceImpl.class);
    @Resource
    private IMemberBaseInfoDao memberBaseInfodao;
    @Resource
    private IRong360MemberBaseInfoDao rong360MemberBaseInfoDao;

    @Value("${db_aesKey}")
    private String myAesKey;

    @Value("${rest_aesKey}")
    private String aesKey;

    /**
     * 查询本地会员信息
     */
    @Override
    public MemberBaseInfo selectBaseInfoByMemberId(String memberId) {
        return memberBaseInfodao.selectByMemberId(memberId);
    }

    @Override
    public MemberBaseInfo selectBaseInfoById(Integer id) {
        return memberBaseInfodao.selectBaseInfoById(id);
    }

    @Override
    public List<MemberBaseInfo> queryLoanMembersPage(String startTime, String endTime, int startRow, int pageSize) {
        List<MemberBaseInfo> memberBaseInfos= memberBaseInfodao.queryLoanMembersPage(startTime, endTime, startRow,  pageSize);
        if (CollectionUtils.isNotEmpty(memberBaseInfos)){
            for (MemberBaseInfo memberBaseInfo:memberBaseInfos){
                encrypt(memberBaseInfo);
            }
        }
        return memberBaseInfos;
    }

    @Override
    public MemberBaseInfo selectBaseInfoByAccount(String mobile) {
        MemberBaseInfo baseInfo = memberBaseInfodao.selectByAccount(mobile);
        if (baseInfo != null) {
            baseInfo.setAuthMobile(AesECBUtil.encrypt(baseInfo.getAuthMobile(), aesKey));
            baseInfo.setAccount(AesECBUtil.encrypt(baseInfo.getAccount(), aesKey));
            baseInfo.setEmergencyContactMobile(AesECBUtil.encrypt(baseInfo.getEmergencyContactMobile(), aesKey));
            baseInfo.setEmergencyContactMobile2nd(AesECBUtil.encrypt(baseInfo.getEmergencyContactMobile2nd(), aesKey));
            baseInfo.setCarNo(AesECBUtil.encrypt(baseInfo.getCarNo(), aesKey));
            baseInfo.setWorkUnitPhone(AesECBUtil.encrypt(baseInfo.getWorkUnitPhone(), aesKey));
            baseInfo.setUserName(baseInfo.getUserName());
            baseInfo.setEmergencyContactName(baseInfo.getEmergencyContactName());
            baseInfo.setEmergencyContactName2nd(baseInfo.getEmergencyContactName2nd());
            baseInfo.setMail(AesECBUtil.encrypt(baseInfo.getMail(), aesKey));
            baseInfo.setIdNo(AesECBUtil.encrypt(baseInfo.getIdNo(), aesKey));
        }
        return baseInfo;
    }


    private void encrypt(MemberBaseInfo memberBaseInfo) {
        memberBaseInfo.setAccount(AesECBUtil.encrypt(memberBaseInfo.getAccount(),aesKey));
        memberBaseInfo.setAuthMobile(AesECBUtil.encrypt(memberBaseInfo.getAuthMobile(),aesKey));
        memberBaseInfo.setEmergencyContactMobile2nd(AesECBUtil.encrypt(memberBaseInfo.getEmergencyContactMobile2nd(),aesKey));
        memberBaseInfo.setEmergencyContactMobile(AesECBUtil.encrypt(memberBaseInfo.getEmergencyContactMobile(),aesKey));
        memberBaseInfo.setIdNoSecret(AesECBUtil.encrypt(memberBaseInfo.getIdNoSecret(),aesKey));
        memberBaseInfo.setWorkUnitPhone(AesECBUtil.encrypt(memberBaseInfo.getWorkUnitPhone(),aesKey));
        memberBaseInfo.setCarNo(AesECBUtil.encrypt(memberBaseInfo.getCarNo(),aesKey));
        memberBaseInfo.setMail(AesECBUtil.encrypt(memberBaseInfo.getMail(),aesKey));
        memberBaseInfo.setSumFinanceCorpCount(memberBaseInfo.getSumFinanceCorpCount());
        memberBaseInfo.setMaxCreditLimitAmount(memberBaseInfo.getMaxCreditLimitAmount());
    }


}
