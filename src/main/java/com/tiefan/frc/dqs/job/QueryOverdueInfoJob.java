package com.tiefan.frc.dqs.job;

import com.alibaba.fastjson.JSONObject;
import com.tiefan.fbs.fsp.base.core.utils.JsonUtil;
import com.tiefan.frc.dqs.dao.IOverdueMobileDataDao;
import com.tiefan.frc.dqs.dao.impl.MemberBaseInfoDaoImpl;
import com.tiefan.frc.dqs.domain.MemberBaseInfo;
import com.tiefan.frc.dqs.domain.OverdueMobileData;
import com.tiefan.frc.dqs.dto.ResponseVo;
import com.tiefan.frc.dqs.dto.lms.OverdueInfoDto;
import com.tiefan.frc.dqs.service.channel.ILmsChannelService;
import com.tiefan.frc.dqs.support.cache.OverDueMobileCache;
import com.tiefan.frc.dqs.support.utils.DateTimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * 从贷后获取用户逾期数据
 */
@Component
public class QueryOverdueInfoJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryOverdueInfoJob.class);

    //SBC:分期商城; FYP:代你还;EMM:现金分期;
    enum PRODUCT_TYPE {
        SBC, FYP, EMM, XYK
    }

    @Resource
    private ILmsChannelService lmsChannelService;
    @Resource
    private IOverdueMobileDataDao overdueMobileDataDao;
    @Resource
    private MemberBaseInfoDaoImpl memberBaseInfoDao;

    public void queryOverdueInfo(String params) throws Exception {
        JSONObject param = JSONObject.parseObject(params);
        String startTime = param.getString("startTime");
        String endTime = param.getString("endTime");
        if (StringUtils.isBlank(startTime)) {
            startTime = DateTimeUtil.getZeroDate(new Date(), -1);
        }
        if (StringUtils.isBlank(endTime)) {
            endTime = DateTimeUtil.getZeroDate(new Date(), 1);
        }
        Map<String, Object> map = new HashMap<>(5);
        map.put("updateTimeBegin", startTime);
        map.put("updateTimeEnd", endTime);
        map.put("limit", 100);
        LOGGER.info("开始同步逾期数据,startTime={},endTime={}", startTime, endTime);
        for (Enum type : PRODUCT_TYPE.values()) {
            long maxId = 0;
            //起始ID,第一次查询为null，下一次为上一次查询列表的最后一条数据的id字段值
            map.put("offsetId", null);
            //SBC:分期商城; FYP:代你还;EMM:现金分期;
            map.put("productType", type.name());
            ResponseVo responseVo = lmsChannelService.queryOverdueInfo(map);
            if (responseVo == null || !responseVo.isSuccess()) {
                throw new Exception("调用贷后接口失败");
            }
            List<OverdueInfoDto> list = JsonUtil.toList(responseVo.getData(), OverdueInfoDto.class);
            while (CollectionUtils.isNotEmpty(list)) {
                maxId = getMaxId(list, maxId, type);
                if (maxId == 0) {
                    LOGGER.warn("逾期数据同步maxId=0");
                    return;
                }
                map.put("offsetId", maxId);
                responseVo = lmsChannelService.queryOverdueInfo(map);
                if (responseVo == null || !responseVo.isSuccess()) {
                    throw new Exception("调用贷后接口失败");
                }
                list = JsonUtil.toList(responseVo.getData(), OverdueInfoDto.class);
            }
            LOGGER.info("逾期数据处理结束produceType={}，start={}", type.name(), startTime);
        }
        updateCache();
        LOGGER.info("更新逾期数据缓存完成，size={}", OverDueMobileCache.getInstance().size());
    }

    private long getMaxId(List<OverdueInfoDto> list, long maxId, Enum type) {
        for (OverdueInfoDto overdueInfoDto : list) {
            if (StringUtils.isBlank(overdueInfoDto.getMemberId())) {
                continue;
            }
            if (maxId < overdueInfoDto.getId()) {
                maxId = overdueInfoDto.getId();
            }
            MemberBaseInfo memberBaseInfo = memberBaseInfoDao.selectByMemberId(overdueInfoDto.getMemberId());
            if (memberBaseInfo == null || StringUtils.isBlank(memberBaseInfo.getAccount())) {
                continue;
            }
            OverdueMobileData data = new OverdueMobileData();
            data.setMobile(Long.valueOf(memberBaseInfo.getAccount()));
            //逾期天数
            Integer overdueDays = overdueInfoDto.getOverdueDays();
            if (overdueInfoDto.getPayOffFlag() == 1) {
                overdueDays = 0;
            }
            //逾期额度
            if (PRODUCT_TYPE.SBC == type) {
                data.setMallOverdueDays(overdueDays);
            } else if (PRODUCT_TYPE.EMM == type) {
                data.setCashOverdueDays(overdueDays);
            } else if (PRODUCT_TYPE.FYP == type) {
                data.setDnhOverdueDays(overdueDays);
            } else if (PRODUCT_TYPE.XYK == type) {
                data.setOfflineOverdueDays(overdueDays);
            }
            //更新数据库
            if (overdueMobileDataDao.selectByMobile(data.getMobile()) == null) {
                overdueMobileDataDao.insert(data);
            } else {
                overdueMobileDataDao.updateByMobile(data);
            }
        }
        return maxId;
    }


    private void updateCache() {
        int pageSize = 1000;
        int startRow = 0;
        Date beginTime = DateTimeUtil.getZeroDate();
        Date endTime = new Date();
        List<OverdueMobileData> all = new ArrayList<>(1024);
        while (true) {
            List<OverdueMobileData> list = overdueMobileDataDao.selectAllByPage(pageSize, startRow, beginTime, endTime);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            all.addAll(list);
            startRow += pageSize;
        }
        if (CollectionUtils.isEmpty(all)) {
            return;
        }
        all.sort((a, b) -> {
            return compare(a, b);
        });
        OverDueMobileCache.refesh(all);
    }


    private boolean addFlag(OverdueMobileData overdueMobileData) {
        return OverDueMobileCache.addFlag(overdueMobileData);
    }

    @PostConstruct
    protected void initOverDueMobileCache() {
        int pageSize = 1000;
        int startRow = 0;
        LOGGER.info("初始化逾期账号");
        while (true) {
            List<OverdueMobileData> list = overdueMobileDataDao.selectAllByPage(pageSize, startRow, null, null);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            for (int i = 0; i < list.size(); i++) {
                OverdueMobileData overdueMobileData = list.get(i);
                if (addFlag(overdueMobileData)) {
                    OverDueMobileCache.getInstance().add(overdueMobileData);
                }
            }
            startRow += pageSize;
        }
        OverDueMobileCache.getInstance().sort((a, b) -> {
            return compare(a, b);
        });
        LOGGER.info("初始化逾期账号结束,size={}", OverDueMobileCache.getInstance().size());
    }

    private int compare(OverdueMobileData a, OverdueMobileData b) {
        return OverDueMobileCache.compare(a, b);
    }

}
