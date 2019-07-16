package com.tiefan.frc.dqs.fsp;

import com.tiefan.fbs.fsp.client.config.ConsumerConfig;
import com.tiefan.fbs.fsp.client.message.MessageSender;
import com.tiefan.frc.dqs.domain.RiskItemGroupFsp;
import com.tiefan.frc.dqs.job.DbCacheJob;
import com.tiefan.frc.dqs.tool.DbCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by georgewu on 2019/5/27.
 */
public class FsConsumerConfigScanDb extends ConsumerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FsConsumerConfigScanDb.class);
    @Autowired
    DbCacheJob dbCacheJob;
    @Override
    public void setReferenceService(List<String> list) {
        try {
            Map<Integer, RiskItemGroupFsp> groupFspMap = DbCache.getInstance().getRiskItemGroupFspMap();
            if(groupFspMap!=null) {
                for (RiskItemGroupFsp fsp : groupFspMap.values()) {
                    if (StringUtils.isNotBlank(fsp.getFsp())) {
                        list.add(fsp.getFsp().trim());
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error("加载数据库规则项fsp失败",e);
        }
        super.setReferenceService(list);

        new Thread("刷新数据库FSP配置"){
            @Override
            public void run() {
                while(true){
                    try{
                        Thread.sleep(10000);
                        List<String> serviceNames = new ArrayList<String>();
                        Map<Integer, RiskItemGroupFsp> groupFspMap = DbCache.getInstance().getRiskItemGroupFspMap();
                        if(groupFspMap!=null) {
                            for (RiskItemGroupFsp fsp : groupFspMap.values()) {
                                if (StringUtils.isNotBlank(fsp.getFsp())) {
                                    if(!list.contains(fsp.getFsp().trim())){
                                        serviceNames.add(fsp.getFsp().trim());
                                        list.add(fsp.getFsp().trim());
                                    }
                                }
                            }
                            if(CollectionUtils.isNotEmpty(serviceNames)){
                                ConsumerConfig consumerConfig = new ConsumerConfig();
                                consumerConfig.setReferenceService(serviceNames);
                                MessageSender.subscribe(consumerConfig, false, 0);
                            }
                        }
                    }catch (Exception e){
                        LOGGER.error("刷新数据库FSP配置异常",e);
                    }
                }
            }
        }.start();
    }
}
