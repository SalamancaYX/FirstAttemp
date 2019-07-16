package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IRiskRuleItemDao;
import com.tiefan.frc.dqs.domain.RiskRuleItem;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Repository
public class RiskRuleItemDaoImpl implements IRiskRuleItemDao {
    private static final String NAMESPACE = "com.tiefan.frc.dqs.dao.IRiskRuleItemDao.";

    @Resource(name = "sqlSessionTemplate")
    private SqlSessionTemplate sst;
    @Override
    public List<RiskRuleItem> queryAll(){
        return sst.selectList(NAMESPACE + "queryAll");
    }
}