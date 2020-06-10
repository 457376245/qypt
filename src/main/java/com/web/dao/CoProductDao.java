package com.web.dao;

import com.web.model.CoProdGroup;
import com.web.model.CoProduct;
import java.util.List;
import java.util.Map;

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
 * @date: 2020/5/23 14:39
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Repository("com.web.dao.CoProductDao")
public interface CoProductDao {
    int deleteByPrimaryKey(Long productId);

    int insertSelective(CoProduct record);

    CoProduct selectByPrimaryKey(Long productId);

    int updateByPrimaryKeySelective(CoProduct record);

    int batchInsert(@Param("list") List<CoProduct> list);

    List<CoProduct> queryProduct(Map<String, Object> params);

    Integer selectByProductCodeCount(Map<String,Object> params);
}