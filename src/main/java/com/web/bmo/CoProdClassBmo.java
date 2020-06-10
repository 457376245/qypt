package com.web.bmo;

import com.web.model.CoProdClass;
import com.web.model.CoProdGroup;

import java.util.Map;

/**
 * <p>
 * 商品分类
 * </p>
 *
 * @package: com.web.bmo
 * @description: 商品分类
 * @author: wangzx8
 * @date: 2020/5/22 16:44
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public interface CoProdClassBmo {
    /**
     * 获取分组列表
     * @param params
     * @return
     */
    Map<String, Object> qryClassList(Map<String, Object> params);

    Map<String, Object> addProdClass(Map<String, Object> params);

    Map<String, Object> editProdClass(Map<String, Object> params);

    CoProdClass selectByPrimaryKey(String classCode);
}
