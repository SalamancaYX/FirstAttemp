package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.ITradeOrderDetailDao;
import com.tiefan.frc.dqs.domain.TradeOrderDetail;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Repository
public class TradeOrderDetailDaoImpl extends BaseDaoImpl implements ITradeOrderDetailDao {


    @Override
    public TradeOrderDetail select(String memberId) {
        Map map = new HashMap();
        map.put("memberId",memberId);
        return findObjByParameter(NAMESPACE + "select",map , TradeOrderDetail.class);
    }
    @Override
    public TradeOrderDetail selectFirst(String memberId) {
        Map map = new HashMap();
        map.put("memberId",memberId);
        return findObjByParameter(NAMESPACE + "selectFirst",map , TradeOrderDetail.class);
    }

}
