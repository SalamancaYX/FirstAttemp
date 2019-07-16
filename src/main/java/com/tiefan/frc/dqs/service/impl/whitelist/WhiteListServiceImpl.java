package com.tiefan.frc.dqs.service.impl.whitelist;

import com.tiefan.frc.dqs.dao.IRiskListWhiteDao;
import com.tiefan.frc.dqs.domain.RiskListWhite;
import com.tiefan.frc.dqs.service.whitelist.IWhiteListService;
import com.tiefan.frc.dqs.support.constants.WhiteType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class WhiteListServiceImpl implements IWhiteListService {
    private final static Logger LOGGER = LoggerFactory.getLogger(WhiteListServiceImpl.class);

    @Resource
    private IRiskListWhiteDao whiteDao;
    /**
     * @param account account
     * @return RiskListWhite RiskListWhite
     */
    public RiskListWhite selectByAccount(String account) {
        return whiteDao.selectByAccount(account);
    }

    @Override
    public Boolean isInWhiteList(String account, String idNo) {
        try {
            List<Map> values = new ArrayList<>();
            if (StringUtils.isNotBlank(account)) {
                Map map = new HashMap();
                map.put("type", WhiteType.PHONE.getCode());
                map.put("value", account);
                values.add(map);
            }
            if (StringUtils.isNotBlank(idNo)) {
                Map map = new HashMap();
                map.put("type", WhiteType.IDCARD.getCode());
                map.put("value", idNo);
                values.add(map);
            }

            return CollectionUtils.isNotEmpty(whiteDao.whiteList(values));
        } catch (Exception e) {
            LOGGER.error("查询是否在白名单中异常，account={},idNo={},error={}",account, idNo, e);
           return false;
        }
    }
}
