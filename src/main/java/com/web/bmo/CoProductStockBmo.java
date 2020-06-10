package com.web.bmo;

import com.web.model.CoProductStock;

import java.util.Map;

/**
 * <p>
 * 库存服务
 * </p>
 *
 * @package: com.web.bmo
 * @description: 库存服务
 * @author: wangzx8
 * @date: 2020/5/29 17:05
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public interface CoProductStockBmo {

    CoProductStock selectByPrimaryKey(Long productId);

    /**
     * 添加库存
     * @param params
     * @return
     */
    Map<String, Object> addProductStock(Map<String, Object> params);
}
