package com.tiefan.frc.dqs.job;

import com.tiefan.frc.dqs.dao.*;
import com.tiefan.frc.dqs.domain.*;
import com.tiefan.frc.dqs.tool.DbCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DbCacheJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbCacheJob.class);

    @Resource
    private IRiskDictListDao riskDictListDao;
    @Resource
    private IRiskItemGroupDao riskItemGroupDao;
    @Resource
    private IRiskItemGroupFspDao riskItemGroupFspDao;
    @Resource
    private IRiskItemFspJsonpathDao riskItemGroupFspJsonpathDao;
    @Resource
    private IRiskRuleItemDao riskRuleItemDao;


    @PostConstruct
    public void initCache() {
        init();
        //定时扫描版本更新
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2 * 60 * 1000);
                    init();
                } catch (Exception e) {
                    LOGGER.error("查询数据库常量失败", e);
                }

            }
        }).start();
    }

    private void init() {
        setRiskDictList();
        setRiskItemGroup();
        setRiskItemGroupFsp();
        setRiskItemGroupJsonPath();
        setRiskRuleItemsList();
    }

    private void setRiskRuleItemsList() {
        try {
            List<RiskRuleItem> riskRuleItemss = riskRuleItemDao.queryAll();
            Map<Integer,RiskRuleItem> idMap=new HashMap<>();
            for(RiskRuleItem riskRuleItem:riskRuleItemss){
                idMap.put(riskRuleItem.getId().intValue(),riskRuleItem);
            }
            DbCache.getInstance().setRiskRuleItemsIdList(idMap);
            LOGGER.info("刷新缓存-RiskRuleItemsList");
        } catch (Exception e) {
            LOGGER.error("刷新缓存-RiskRuleItemsList异常", e);
        }
    }

    private void setRiskDictList() {
        try {
            LOGGER.info("刷新缓存-RiskCustomClassValueLis");
            List<RiskDictList> list = riskDictListDao.selectDictListAll();
            DbCache dbCache = DbCache.getInstance();
            Map<String, List<RiskDictList>> tmpMap=new HashMap<>();
            for (RiskDictList dictList : list) {
                List<RiskDictList> tmp = tmpMap.get(dictList.getKey());
                if (tmp == null) {
                    tmp = new ArrayList<>();
                    tmpMap.put(dictList.getKey(), tmp);
                }
                tmp.add(dictList);
            }
            dbCache.setRiskCustomClassValue(tmpMap);
        } catch (Exception e) {
            LOGGER.error("刷新缓存-RiskCustomClassValueLis异常", e);
        }
    }
    private void setRiskItemGroup() {
        try {
            DbCache dbCache = DbCache.getInstance();
            List<RiskItemGroup> groups = riskItemGroupDao.selectAll();
            Map<Integer, RiskItemGroup> groupMap = new HashMap<>();
            for (RiskItemGroup group : groups) {
                groupMap.put(group.getId(), group);
            }
            dbCache.setRiskItemGroupMap(groupMap);
        } catch (Exception e) {
            LOGGER.info("刷新缓存异常");
        }
    }


    private void setRiskItemGroupFsp() {
        try {
            DbCache dbCache = DbCache.getInstance();
            List<RiskItemGroupFsp> groups = riskItemGroupFspDao.selectAll();
            Map<Integer, RiskItemGroupFsp> groupMap = new HashMap<>();
            for (RiskItemGroupFsp group : groups) {
                groupMap.put(group.getGroupId(), group);
            }
            dbCache.setRiskItemGroupFspMap(groupMap);
        } catch (Exception e) {
            LOGGER.info("刷新缓存异常");
        }
    }

    private void setRiskItemGroupJsonPath() {
        try {
            DbCache dbCache = DbCache.getInstance();
            List<RiskItemGroupFspJsonpath> groups = riskItemGroupFspJsonpathDao.selectAll();
            Map<Integer, List<RiskItemGroupFspJsonpath>> groupMap = new HashMap<>();
            for (RiskItemGroupFspJsonpath group : groups) {
                if(groupMap.get(group.getGroupId())==null){
                    groupMap.put(group.getGroupId(),new ArrayList<>());
                }
                groupMap.get(group.getGroupId()).add(group);
            }
            dbCache.setRiskItemGroupFspJsonpathMap(groupMap);
        } catch (Exception e) {
            LOGGER.info("刷新缓存异常");
        }
    }

}
