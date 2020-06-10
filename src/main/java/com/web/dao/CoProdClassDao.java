package com.web.dao;

import com.web.model.CoProdClass;
import java.util.List;
import java.util.Map;

import com.web.model.CoProdGroup;
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
 * @date: 2020/5/22 16:31
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Repository("com.web.dao.CoProdClassDao")
public interface CoProdClassDao {
    int deleteByPrimaryKey(String classCode);

    int insert(CoProdClass record);

    int insertSelective(CoProdClass record);

    CoProdClass selectByPrimaryKey(String classCode);
    /**
     *
     * @param params classCode  新编码
     *               oldClassCode 旧编码
     * @return
     */
    long selectCountByClassCode(Map<String,Object> params);

    int updateByPrimaryKeySelective(CoProdClass record);

    int updateByPrimaryKey(CoProdClass record);

    int batchInsert(@Param("list") List<CoProdClass> list);

    List<CoProdClass> queryClass(Map<String,Object> paramMap);
}