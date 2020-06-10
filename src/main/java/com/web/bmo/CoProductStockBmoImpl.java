package com.web.bmo;

import com.web.common.Constants;
import com.web.dao.CoProductDao;
import com.web.dao.CoProductStockDao;
import com.web.model.CoProductStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
@Service("com.web.bmo.CoProductStockBmoImpl")
public class CoProductStockBmoImpl implements CoProductStockBmo {

    @Autowired
    private CoProductStockDao coProductStockDao;


    @Override
    public CoProductStock selectByPrimaryKey(Long productId) {
        return coProductStockDao.selectByPrimaryKey(productId);
    }

    @Override
    public Map<String, Object> addProductStock(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "新增库存失败");
        Integer addResult = coProductStockDao.addProductStock(params);
        if (addResult != null && addResult > 0) {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "新增产品信息成功");
        }

        return turnMap;
    }
}
