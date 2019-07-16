package com.tiefan.frc.dqs.service.impl;

import com.tiefan.frc.dqs.domain.OverdueMobileData;
import com.tiefan.frc.dqs.dto.ResponseVo;
import com.tiefan.frc.dqs.service.IOverdueDataService;
import com.tiefan.frc.dqs.support.cache.OverDueMobileCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OverdueDataServiceImpl implements IOverdueDataService {
    @Override
    public ResponseVo queryOverDueCount(List<String> mobiles) {
        List<Long> mobiles2 = mobiles.stream().filter(m -> StringUtils.isNotBlank(m)).map(m -> NumberUtils.toLong(m))
                .distinct().collect(Collectors.toList());
        int overdueCount = 0;
        mobiles2.sort((a, b) -> {
            return OverDueMobileCache.compare(a, b);
        });
        List<OverdueMobileData> list = OverDueMobileCache.getInstance();
        int j = 0;
        int cacheSize = list.size();
        for (int i = 0; i < mobiles2.size(); i++) {
            long mobile = mobiles2.get(i).longValue();
            for (; j < cacheSize; j++) {
                OverdueMobileData overdueMobileData = list.get(j);
                if (mobile == overdueMobileData.getMobile().longValue()) {
                    overdueCount++;
                    break;
                } else if (mobile < overdueMobileData.getMobile().longValue()) {
                    break;
                }
            }
            if (j == cacheSize) {
                break;
            }
        }
        ResponseVo responseVo = new ResponseVo();
        responseVo.setData(overdueCount);
        responseVo.setSuccess(true);
        return responseVo;
    }
}
