package com.tiefan.frc.dqs.service.impl;

import com.tiefan.frc.dqs.dao.IMicrofinanceFlagDao;
import com.tiefan.frc.dqs.domain.MicrofinanceFlag;
import com.tiefan.frc.dqs.service.IMicrofinanceFlagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MicrofinanceFlagServiceImpl implements IMicrofinanceFlagService {
    @Resource
    private IMicrofinanceFlagDao microfinanceFlagDao;
    @Override
    public List<MicrofinanceFlag> selectMemberIdPage(String startTime, String endTime, int startRow, int pageSize) {
        return microfinanceFlagDao.selectMemberIdPage(startTime, endTime, startRow, pageSize);
    }
}
