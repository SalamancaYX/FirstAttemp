package com.tiefan.frc.dqs.service.impl.behavior;

import com.tiefan.frc.dqs.dao.behavior.IBehaviorAnalysisDao;
import com.tiefan.frc.dqs.dto.behavior.BehaviorBaseData;
import com.tiefan.frc.dqs.service.behavior.IBehaviorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BehaviorServiceImpl implements IBehaviorService {
    @Resource
    private IBehaviorAnalysisDao behaviorAnalysisDao;


    @Override
    public List<BehaviorBaseData> queryLoginData(String mobile) {
        return behaviorAnalysisDao.queryLoginData(mobile);
    }
}
