package com.tiefan.frc.dqs.dao.impl;

import com.tiefan.frc.dqs.dao.IBaseDao;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 此类描述的是：IBaseDao实现类
 *
 * @author fujian2
 */
@Repository
public class BaseDaoImpl implements IBaseDao {

    @Resource(name = "sqlSessionTemplate")
    private SqlSessionTemplate sst;

    /**
     * 此方法描述的是：
     *
     * @param <T>       类模板
     * @param statement 执行的sql
     * @param clazz     返回的类
     * @return T
     */
    public <T> T findObj(String statement, Class<T> clazz) {
        return sst.selectOne(statement);
    }

    /**
     * 根据statement语句查询，结果不为List，而是一个POJO对象。
     *
     * @param <T>       类模板
     * @param clazz     返回的类
     * @param id        id
     * @param statement sql
     * @return T
     */
    public <T> T findObjById(String statement, String id, Class<T> clazz) {
        T ret = null;
        if (null == id) {
            ret = sst.selectOne(statement);
        } else {
            ret = sst.selectOne(statement, id);
        }
        return ret;
    }

    /**
     * 根据statement语句查询，结果不为List，而是一个POJO对象。
     *
     * @param <T>       类模板
     * @param clazz     返回的类
     * @param id        id
     * @param statement sql
     * @return T
     */
    public <T> T findObjById(String statement, Integer id, Class<T> clazz) {
        T ret = null;
        if (null == id) {
            ret = sst.selectOne(statement);
        } else {
            ret = sst.selectOne(statement, id);
        }
        return ret;
    }

    /**
     * 根据statement语句和parameter参数查询确定一个POJO对象。
     *
     * @param <T>       类模板
     * @param clazz     返回的类
     * @param parameter 输入的参数
     * @param statement sql
     * @return T
     */
    public <T> T findObjByParameter(String statement, Object parameter, Class<T> clazz) {
        T ret = null;
        if (null == parameter) {
            ret = sst.selectOne(statement);
        } else {
            ret = sst.selectOne(statement, parameter);
        }

        return ret;
    }

    /**
     * 根据statement语句查询，返回结果为所有记录。
     *
     * @param <T>       类模板
     * @param clazz     返回的类
     * @param statement sql
     * @return T
     */
    public <T> List<T> findAll(String statement, Class<T> clazz) {
        return sst.selectList(statement);
    }

    /**
     * 查询所有结果
     *
     * @param statement stat
     * @param params    params
     * @param clazz     clazz
     * @param <T>       t
     * @return list
     */
    public <T> List<T> findAll(String statement, Object params, Class<T> clazz) {
        return sst.selectList(statement, params);
    }

    /**
     * 根据statement语句和parameter参数查询，返回结果为符合条件的集合。
     *
     * @param <T>       类模板
     * @param clazz     返回的类
     * @param parameter 输入的参数
     * @param statement sql
     * @return List
     */
    public <T> List<T> findListByParameter(String statement, Object parameter, Class<T> clazz) {
        if (null == parameter) {
            return findAll(statement, clazz);
        } else {
            return sst.selectList(statement, parameter);
        }
    }

//    public <T> List<T> findListByParameterBySlave(String statement, Object parameter, Class<T> clazz) {
//        if (null == parameter) {
//            return findAll(statement, clazz);
//        } else {
//            return sstSlave.selectList(statement, parameter);
//        }
//    }

    /**
     * @param <T>       类模板
     * @param clazz     返回的类
     * @param parameter 输入的参数
     * @param statement sql
     * @param page      页
     * @param pageSize  每页大小
     * @return List
     */
    public <T> List<T> findListByPage(String statement, Object parameter, int page, int pageSize, Class<T> clazz) {
        return sst.selectList(statement, parameter, new RowBounds(page, pageSize));
    }

    /**
     * @param parameter 参数
     * @param statement sql
     * @return int
     */
    public int findCount(String statement, Object parameter) {
        Object count = sst.selectOne(statement, parameter);
        return null == count ? 0 : (Integer) count;
    }

//    public int findCountSlave(String statement, Object parameter) {
//        Object count = this.sstSlave.selectOne(statement, parameter);
//        return null == count ? 0 : (Integer) count;
//    }

    /**
     * @param statement sql
     * @return int
     */
    public int findCount(String statement) {
        Object count = sst.selectOne(statement);
        return null == count ? 0 : (Integer) count;
    }

    /**
     * @param statement sql
     * @return int
     */
    public int findCountSlave(String statement) {
        Object count = sst.selectOne(statement);
        return null == count ? 0 : (Integer) count;
    }

    /**
     * @param parameter 参数
     * @param statement sql
     * @return int
     */
    public int save(String statement, Object parameter) {
        return sst.insert(statement, parameter);
    }

    /**
     * @param parameter 参数
     * @param statement sql
     * @return int
     */
    public int update(String statement, Object parameter) {
        return sst.update(statement, parameter);
    }

    /**
     * @param statement sql
     * @return int
     */
    public int delete(String statement) {
        return sst.delete(statement);
    }

    /**
     * @param parameter 参数
     * @param statement sql
     * @return int
     */
    public int delete(String statement, Object parameter) {
        return sst.delete(statement, parameter);
    }

    /**
     * sst
     *
     * @return the sst
     */

    public SqlSessionTemplate getSst() {
        return sst;
    }

    /**
     * @param sst the sst to set
     */

    public void setSst(SqlSessionTemplate sst) {
        this.sst = sst;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Map> selectMap(String statement, Object parameter, String mapKey) {
        return sst.selectMap(statement, parameter, mapKey);
    }
}
