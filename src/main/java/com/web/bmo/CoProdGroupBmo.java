package com.web.bmo;

import com.web.model.CoProdGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品分组
 * </p>
 *
 * @package: com.web.bmo
 * @description: 商品分组
 * @author: wangzx8
 * @date: 2020/5/20 17:12
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public interface CoProdGroupBmo {

    /**
     * 根据主键物理删除数据
     * @param groupId
     * @return
     */
    int deleteByPrimaryKey(Long groupId);

    /**
     * 插入全部数据
     * @param record
     * @return
     */
    int insert(CoProdGroup record);

    /**
     * 插入不为null的数据
     * @param record
     * @return
     */
    int insertSelective(CoProdGroup record);

    /**
     * 根据主键获取数据
     * @param groupId
     * @return
     */
    CoProdGroup selectByPrimaryKey(Long groupId);

    /**
     * 根据主键修改不为空的
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(CoProdGroup record);

    /**
     * 根据主键全部修改
     * @param record
     * @return
     */
    int updateByPrimaryKey(CoProdGroup record);

    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(@Param("list") List<CoProdGroup> list);

    /**
     * 获取分组列表
     * @param params
     * @return
     */
    Map<String, Object> qryGroupList(Map<String, Object> params);

    Map<String, Object> addProdGroup(Map<String, Object> params);

    Map<String, Object> editProdGroup(Map<String, Object> params);
}
