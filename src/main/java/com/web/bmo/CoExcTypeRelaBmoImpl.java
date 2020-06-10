package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.CoExcObjDao;
import com.web.dao.CoExcTypeRelaDao;
import com.web.model.CoExcObj;
import com.web.model.CoExcTypeRela;
import com.web.util.MapUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品和分类关系
 * </p>
 *
 * @package: com.web.bmo
 * @description: 商品和分类关系
 * @author: wangzx8
 * @date: 2020/6/2 16:11
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Service("com.web.bmo.CoExcTypeRelaBmoImpl")
public class CoExcTypeRelaBmoImpl implements CoExcTypeRelaBmo {

    @Autowired
    private CoExcTypeRelaDao coExcTypeRelaDao;


    @Autowired
    private CoExcObjDao coExcObjDao;


    @Override
    public Map<String, Object> queryExcObjByTypeId(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(params, "page");
        int pageSize = MapUtil.asInt(params, "limit");
        PageHelper.startPage(pageNum, pageSize);
        List<CoExcTypeRela> list = coExcTypeRelaDao.queryExcObjByTypeId(params);
        PageInfo<CoExcTypeRela> info = new PageInfo<>(list);
        resultMap.put("data", info.getList());
        resultMap.put("count", info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> queryExcObjByNotTypeId(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(params, "page");
        int pageSize = MapUtil.asInt(params, "limit");
        PageHelper.startPage(pageNum, pageSize);
        List<CoExcObj> list = coExcObjDao.queryExcObjByNotTypeId(params);
        PageInfo<CoExcObj> info = new PageInfo<>(list);
        resultMap.put("data", info.getList());
        resultMap.put("count", info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> addExcTypeRel(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "新增分类商品关联信息失败");
        Long typeId = MapUtils.getLong(params,"typeId");
        Long objId = MapUtils.getLong(params,"objId");

        Integer count = coExcTypeRelaDao.selectCountByTypeIdAndObjId(params);
        if(count == 0) {
            CoExcTypeRela coExcTypeRela = new CoExcTypeRela();
            coExcTypeRela.setObjId(objId);
            coExcTypeRela.setTypeId(typeId);
            LocalDateTime nowLocalDateTime = LocalDateTime.now();
            coExcTypeRela.setAddTime(nowLocalDateTime);
            coExcTypeRela.setStatusDate(nowLocalDateTime);
            coExcTypeRela.setUpdateDate(nowLocalDateTime);
            coExcTypeRela.setStatusCd("101");

            Integer addResult = coExcTypeRelaDao.insertSelective(coExcTypeRela);
            if (addResult != null && addResult > 0) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "新增分类商品关联信息成功");
            }
        }else{
            turnMap.put(Constants.RESULT_MSG_STR, "该商品已添加到当前分类");
        }
        return turnMap;
    }

    @Override
    public Map<String, Object> editExcTypeRel(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "修改分类商品关联信息失败");
        Long objTypeId = MapUtils.getLong(params,"objTypeId");
        Long typeId = MapUtils.getLong(params,"typeId");
        Long objId = MapUtils.getLong(params,"objId");
        CoExcTypeRela oldCoExcTypeRela = coExcTypeRelaDao.selectByPrimaryKey(objTypeId);
        String statusCd = MapUtils.getString(params,"statusCd");
        CoExcTypeRela coExcTypeRela = new CoExcTypeRela();
        coExcTypeRela.setObjTypeId(objTypeId);
        coExcTypeRela.setObjId(objId);
        coExcTypeRela.setTypeId(typeId);
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        if(!oldCoExcTypeRela.getStatusCd().equals(statusCd)){
            coExcTypeRela.setStatusDate(nowLocalDateTime);
            coExcTypeRela.setStatusCd(statusCd);
        }
        coExcTypeRela.setUpdateDate(nowLocalDateTime);
        Integer addResult = coExcTypeRelaDao.updateByPrimaryKeySelective(coExcTypeRela);
        if (addResult != null && addResult > 0) {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "修改分类商品关联信息成功");
        }
        return turnMap;
    }
}
