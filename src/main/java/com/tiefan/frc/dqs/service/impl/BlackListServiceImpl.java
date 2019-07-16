package com.tiefan.frc.dqs.service.impl;

import com.tiefan.frc.dqs.dao.IRiskAntifraudListBlackDao;
import com.tiefan.frc.dqs.dao.IRiskJyqMemberDao;
import com.tiefan.frc.dqs.dao.IRiskListBlackDao;
import com.tiefan.frc.dqs.dao.IRiskListGrayDao;
import com.tiefan.frc.dqs.domain.RiskAntifraudListBlack;
import com.tiefan.frc.dqs.domain.RiskJyqMember;
import com.tiefan.frc.dqs.domain.RiskListBlack;
import com.tiefan.frc.dqs.domain.RiskListGray;
import com.tiefan.frc.dqs.service.IBlackListService;
import com.tiefan.frc.dqs.support.constants.BlackGrayType;
import com.tiefan.frc.dqs.support.constants.DqsConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlackListServiceImpl implements IBlackListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlackListServiceImpl.class);

    @Autowired
    protected IRiskListBlackDao iRiskListBlackDao;

    @Autowired
    protected IRiskAntifraudListBlackDao iRiskAntifraudListBlackDao;//反欺诈黑名单

    @Autowired
    protected IRiskListGrayDao iRiskListGrayDao;// 灰名单

    @Autowired
    private IRiskJyqMemberDao riskJyqMemberDao;

    @Override
    public List<RiskListBlack> blackList(String account, String idNo, String cardNo, String holderMobileNo, String deviceId,int isMd5){
        try {
            List<Map> values = new ArrayList<>();
            if (StringUtils.isNotBlank(account)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.PHONE.getCode());
                map.put("value", account);
                values.add(map);
            }
            if (StringUtils.isNotBlank(idNo)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.IDCARD.getCode());
                map.put("value", idNo);
                values.add(map);
            }
            if (StringUtils.isNotBlank(cardNo)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.BANKCARD.getCode());
                map.put("value", cardNo);
                values.add(map);
            }
            if (StringUtils.isNotBlank(holderMobileNo)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.PHONE.getCode());
                map.put("value", holderMobileNo);
                values.add(map);
            }
            if (StringUtils.isNotBlank(deviceId)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.DEVICE.getCode());
                map.put("value", deviceId);
                values.add(map);
            }
            return iRiskListBlackDao.blackList(values,isMd5);
        } catch (Exception e) {
            LOGGER.error("查询黑名单异常", e);
        }
        return null;
    }

    @Override
    public List<RiskAntifraudListBlack> queryInFQZBlackList(String account, String idNo, String cardNo, String holderMobileNo, String deviceId,int isMd5) {
        try {
            List<Map> values = new ArrayList<>();
            if (StringUtils.isNotBlank(account)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.PHONE.getCode());
                map.put("value", account);
                values.add(map);
            }
            if (StringUtils.isNotBlank(idNo)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.IDCARD.getCode());
                map.put("value", idNo);
                values.add(map);
            }
            if (StringUtils.isNotBlank(cardNo)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.BANKCARD.getCode());
                map.put("value", cardNo);
                values.add(map);
            }
            if (StringUtils.isNotBlank(holderMobileNo)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.PHONE.getCode());
                map.put("value", holderMobileNo);
                values.add(map);
            }
            if (StringUtils.isNotBlank(deviceId)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.DEVICE.getCode());
                map.put("value", deviceId);
                values.add(map);
            }
            return iRiskAntifraudListBlackDao.queryListByTypeAndValues(values,isMd5);
        } catch (Exception e) {
            LOGGER.error("查找FQZ黑名单异常", e);
        }
        return null;
    }

    @Override
    public List<RiskJyqMember> queryJyqBlackList(String account, String idNo, String cardNo, String holderMobileNo, String deviceId,int isMd5) {
        try {
            List<Map> values = new ArrayList<>();
            if (StringUtils.isNotBlank(account)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.PHONE.getCode());
                map.put("value", account);
                values.add(map);
            }
            if (StringUtils.isNotBlank(idNo)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.IDCARD.getCode());
                map.put("value", idNo);
                values.add(map);
            }
            if (StringUtils.isNotBlank(cardNo)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.BANKCARD.getCode());
                map.put("value", cardNo);
                values.add(map);
            }
            if (StringUtils.isNotBlank(deviceId)) {
                Map map = new HashMap();
                map.put("type", BlackGrayType.DEVICE.getCode());
                map.put("value", deviceId);
                values.add(map);
            }
            return riskJyqMemberDao.blackList(values,isMd5);
        } catch (Exception e) {
            LOGGER.error("查找急用钱黑名单异常", e);
        }
        return null;
    }
}
