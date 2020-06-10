package com.web.dao;

import com.web.model.CoProdBrand;
import java.util.List;
import java.util.Map;

import com.web.model.CoProdClass;
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
 * @date: 2020/5/22 19:02
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Repository("com.web.dao.CoProdBrandDao")
public interface CoProdBrandDao {
    int deleteByPrimaryKey(String brandCode);

    int insert(CoProdBrand record);

    int insertSelective(CoProdBrand record);

    CoProdBrand selectByPrimaryKey(String brandCode);

    int updateByPrimaryKeySelective(CoProdBrand record);

    int updateByPrimaryKey(CoProdBrand record);

    int batchInsert(@Param("list") List<CoProdBrand> list);

    List<CoProdBrand> queryBrand(Map<String, Object> params);

    Long selectCountByBrandCode(Map<String, Object> params);
}