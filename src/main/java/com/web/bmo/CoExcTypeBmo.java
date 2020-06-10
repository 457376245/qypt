package com.web.bmo;

import com.web.model.CoExcType;

import java.util.Map;

/**
 * <p>
 * 商品分类
 * </p>
 *
 * @package: com.web.bmo
 * @description: 商品分类
 * @author: wangzx8
 * @date: 2020/5/25 10:07
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public interface CoExcTypeBmo {

    /**
     * 获取分组列表
     * @param params
     * @return
     */
    Map<String, Object> qryExcTypeList(Map<String, Object> params);

    Map<String, Object> addExcType(Map<String, Object> params);

    Map<String, Object> editExcType(Map<String, Object> params);

    CoExcType selectByPrimaryKey(Long typeId);
}
