package com.web.dao;

import com.web.model.CoProdGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  商品
 * </p>
 *
 * @package: com.web.dao
 * @description: ${description}
 * @author: wangzx8
 * @date: 2020/5/20 16:59
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Repository("com.web.dao.CoProdGroupDao")
public interface CoProdGroupDao {

    /**
     * 根据主键物理删除数据
     *
     * @param groupId
     * @return
     */
    int deleteByPrimaryKey(Integer groupId);

    /**
     * 插入全部数据
     *
     * @param record
     * @return
     */
    int insert(CoProdGroup record);

    /**
     * 插入不为null的数据
     *
     * @param record
     * @return
     */
    int insertSelective(CoProdGroup record);

    /**
     * 根据主键获取数据
     *
     * @param groupId
     * @return
     */
    CoProdGroup selectByPrimaryKey(Long groupId);

    /**
     * 根据主键修改不为空的
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(CoProdGroup record);

    /**
     * 根据主键全部修改
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(CoProdGroup record);

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int batchInsert(@Param("list") List<CoProdGroup> list);

    List<CoProdGroup> queryGroup(Map<String,Object> paramMap);
}