package com.web.bmo;

import com.web.model.CoProdBrand;
import com.web.model.CoProdClass;

import java.util.Map;

/**
 * <p>
 * 商品品牌
 * </p>
 *
 * @package: com.web.bmo
 * @description: 商品品牌
 * @author: wangzx8
 * @date: 2020/5/22 19:11
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public interface CoProdBrandBmo {

    /**
     * 获取分组列表
     * @param params
     * @return
     */
    Map<String, Object> qryBrandList(Map<String, Object> params);

    Map<String, Object> addProdBrand(Map<String, Object> params);

    Map<String, Object> editProdBrand(Map<String, Object> params);

    CoProdBrand selectByPrimaryKey(String brandCode);
}
