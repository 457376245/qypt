package com.web.dao;

import com.web.model.CoProductStock;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * ${description}
 * </p>
 *
 * @package: com.web.dao
 * @description: ${description}
 * @author: wangzx8
 * @date: 2020/5/29 9:42
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Repository("com.web.dao.CoProductStockDao")
public interface CoProductStockDao {
    int deleteByPrimaryKey(Long productId);

    int insert(CoProductStock record);

    int insertSelective(CoProductStock record);

    CoProductStock selectByPrimaryKey(Long productId);

    int updateByPrimaryKeySelective(CoProductStock record);

    int updateByPrimaryKey(CoProductStock record);

    int batchInsert(@Param("list") List<CoProductStock> list);

    Integer addProductStock(Map<String, Object> params);
}