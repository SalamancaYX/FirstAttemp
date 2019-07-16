package com.tiefan.frc.dqs.dao.impl.lrr;

import com.tiefan.frc.dqs.dao.lrr.IUserAddressbookDao;
import com.tiefan.frc.dqs.domain.lrr.UserAppSmsFavList;
import com.tiefan.frc.dqs.support.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
public class UserAddressbookDaoImpl implements IUserAddressbookDao {
    @Resource(name = "sqlSessionTemplate2")
    private SqlSessionTemplate sst2;
    @Value("${db_aesKey}")
    private String aesKey;
    @Override
    public int insert(UserAppSmsFavList appSmsFavList) {
        if (StringUtils.isBlank(appSmsFavList.getAccount())){
            appSmsFavList.setAccount("");
        }
        Map<String, Object> map = JsonUtil.toMap(appSmsFavList);
        map.put("tableIndex", (int)(Double.valueOf(appSmsFavList.getMemberId()) % 100));
        return sst2.insert(NAMESPACE + "insert", map);
    }

    @Override
    public List<String> selectMd5ByMemberId(String memberId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tableIndex", (int)(Double.valueOf(memberId) % 100));
        map.put("memberId", memberId);
        return sst2.selectList(NAMESPACE + "selectMd5ByMemberId", map);
    }

    @Override
    public UserAppSmsFavList getAddressbookByMemberId(String memberId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tableIndex", (int)(Double.valueOf(memberId) % 100));
        map.put("memberId", memberId);
        return sst2.selectOne(NAMESPACE + "selectLastOneByMemberId", map);
    }

    @Override
    public Integer getAddbookCount(String md5, String memberId){
        Map<String, Object> map = new HashMap<>();
        map.put("md5", md5);
        map.put("memberId", memberId);
        return sst2.selectOne(NAMESPACE + "getAddbookCount", map);
    }
}
