package com.tiefan.frc.dqs.service.impl.risk.channel;

import com.alibaba.fastjson.JSONObject;
import com.tiefan.frc.dqs.domain.RiskItemGroup;
import com.tiefan.frc.dqs.domain.RiskItemGroupFsp;
import com.tiefan.frc.dqs.service.group.item.IGroupItemInvokeService;
import com.tiefan.frc.dqs.service.impl.risk.channel.fsp.FspGroupItemInvokeServiceImpl;
import com.tiefan.frc.dqs.tool.DbCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by georgewu on 2019/5/24.
 */
@Service
public class GroupItemInvokeServiceImpl implements IGroupItemInvokeService {


    @Autowired
    protected FspGroupItemInvokeServiceImpl fspGroupItemTemplate;

    @Override
    public Map invoke(Integer groupId, Map parameters) {
        //查询规则项获取类型   1 fsp  2 其他
        RiskItemGroup group = DbCache.getInstance().getRiskItemGroupMap().get(groupId);
        if(group.getType() == 1){
            return fspGroupItemTemplate.invokeFsp(groupId, parameters);

        }else if(group.getType() == 2){

        }
        return null;
    }

}
