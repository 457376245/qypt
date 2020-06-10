package com.web.dao;

import com.web.model.CoExcType;
import java.util.List;import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 商品分类
 * </p>
 *
 * @package: com.web.dao
 * @description: ${description}
 * @author: wangzx8
 * @date: 2020/5/25 9:57
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Repository("com.web.dao.CoExcTypeDao")
public interface CoExcTypeDao {
    int deleteByPrimaryKey(Long typeId);

    int insertSelective(CoExcType record);

    CoExcType selectByPrimaryKey(Long typeId);

    int updateByPrimaryKeySelective(CoExcType record);

    int batchInsert(@Param("list") List<CoExcType> list);

    List<CoExcType> queryExcType(Map<String, Object> params);

    int selectCountByTypeCode(Map<String, Object> params);
}