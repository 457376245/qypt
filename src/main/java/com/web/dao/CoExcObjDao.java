package com.web.dao;

import com.web.model.CoExcObj;
import java.util.List;
import java.util.Map;

import com.web.model.CoExcType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *      ${description} 
 * </p>
 *
 * @package: com.web.dao
 * @description: ${description}  
 * @author: wangzx8
 * @date: 2020/6/1 11:16
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Repository("com.web.dao.CoExcObjDao")
public interface CoExcObjDao {
    int deleteByPrimaryKey(Long objId);

    int insertSelective(CoExcObj record);

    CoExcObj selectByPrimaryKey(Long objId);

    int updateByPrimaryKeySelective(CoExcObj record);

    int batchInsert(@Param("list") List<CoExcObj> list);

    List<CoExcObj> queryExcObj(Map<String, Object> params);

    Integer selectCountObjCode(Map<String, Object> params);

    List<CoExcObj> queryExcObjByNotTypeId(Map<String, Object> params);

}