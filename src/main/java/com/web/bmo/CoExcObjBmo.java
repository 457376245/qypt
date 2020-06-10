package com.web.bmo;

import com.web.model.CoExcObj;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品
 * </p>
 *
 * @package: com.web.bmo
 * @description: 商品
 * @author: wangzx8
 * @date: 2020/6/1 11:18
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public interface CoExcObjBmo {

    int deleteByPrimaryKey(Long objId);

    int insertSelective(CoExcObj record);

    CoExcObj selectByPrimaryKey(Long objId);

    int updateByPrimaryKeySelective(CoExcObj record);

    Map<String, Object> qryExcObjList(Map<String, Object> params);

    Map<String, Object> addExcObj(Map<String, Object> params);

    Map<String, Object> editExcObj(Map<String, Object> params);

    Map<String, Object> qryProductList(Map<String, Object> params);
}
