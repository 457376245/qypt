package com.web.bmo;

import java.util.Map;

/**
 * <p>
 * 产品和分类关系
 * </p>
 *
 * @package: com.web.bmo
 * @description: 产品和分类关系
 * @author: wangzx8
 * @date: 2020/6/2 16:10
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public interface CoExcTypeRelaBmo {


    /**
     * 查询已经绑定的分类商品
     * @param params
     * @return
     */
    Map<String, Object> queryExcObjByTypeId(Map<String, Object> params);

    Map<String, Object> queryExcObjByNotTypeId(Map<String, Object> params);

    Map<String, Object> addExcTypeRel(Map<String, Object> params);

    Map<String, Object> editExcTypeRel(Map<String, Object> params);
}
