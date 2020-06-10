package com.web.bmo;

import com.web.model.CoProdClass;
import com.web.model.CoProduct;

import java.util.Map;

/**
 * <p>
 * 权益
 * </p>
 *
 * @package: com.web.bmo
 * @description: 权益
 * @author: wangzx8
 * @date: 2020/5/23 14:47
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public interface CoProductBmo {


    /**
     * 获取分组列表
     * @param params
     * @return
     */
    Map<String, Object> qryProductList(Map<String, Object> params);

    /**
     * 新增商品
     * @param params
     * @return
     */
    Map<String, Object> addProduct(Map<String, Object> params);

    /**
     * 修改商品
     * @param params
     * @return
     */
    Map<String, Object> editProduct(Map<String, Object> params);

    /**
     * 查询商品信息
     * @param productId
     * @return
     */
    CoProduct selectByPrimaryKey(Long productId);
}





