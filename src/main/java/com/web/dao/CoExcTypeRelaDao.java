package com.web.dao;

import com.web.model.CoExcObj;
import com.web.model.CoExcType;
import com.web.model.CoExcTypeRela;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * ${description}
 * </p>
 *
 * @package: com.web.dao
 * @description: ${description}
 * @author: wangzx8
 * @date: 2020/6/2 16:07
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Repository("com.web.dao.CoExcTypeRelaDao")
public interface CoExcTypeRelaDao {

    int deleteByPrimaryKey(Long objTypeId);

    int insertSelective(CoExcTypeRela record);

    CoExcTypeRela selectByPrimaryKey(Long objTypeId);

    int updateByPrimaryKeySelective(CoExcTypeRela record);

    int batchInsert(@Param("list") List<CoExcTypeRela> list);

    List<CoExcTypeRela> queryExcObjByTypeId(Map<String, Object> params);


    Integer selectCountByTypeIdAndObjId(Map<String, Object> params);
}