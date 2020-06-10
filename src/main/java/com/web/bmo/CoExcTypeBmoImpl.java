package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.CoExcTypeDao;
import com.web.model.CoExcType;
import com.web.util.MapUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品分类
 * </p>
 *
 * @package: com.web.bmo
 * @description: 商品分类
 * @author: wangzx8
 * @date: 2020/5/25 10:08
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Service("com.web.bmo.CoExcTypeBmoImpl")
public class CoExcTypeBmoImpl implements CoExcTypeBmo {

    @Autowired
    private CoExcTypeDao coExcTypeDao;

    @Override
    public Map<String, Object> qryExcTypeList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(params, "page");
        int pageSize = MapUtil.asInt(params, "limit");
        PageHelper.startPage(pageNum, pageSize);
        List<CoExcType> list = coExcTypeDao.queryExcType(params);
        PageInfo<CoExcType> info = new PageInfo<>(list);
        resultMap.put("data", info.getList());
        resultMap.put("count", info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> addExcType(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "新增分类信息失败");
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        //判断classCode是否存在
        Integer count = coExcTypeDao.selectCountByTypeCode(params);
        if (count == 0) {
            CoExcType coExcType = new CoExcType();
            coExcType.setTypeCode(MapUtils.getString(params, "typeCode"));
            coExcType.setTypeDesc(MapUtils.getString(params, "typeDesc"));
            coExcType.setSortSeq(MapUtils.getInteger(params, "sortSeq"));
            coExcType.setAddTime(nowLocalDateTime);
            coExcType.setStatusDate(nowLocalDateTime);
            coExcType.setUpdateDate(nowLocalDateTime);
            coExcType.setStatusCd(MapUtils.getString(params, "statusCd"));
            Integer addResult = this.coExcTypeDao.insertSelective(coExcType);
            if (addResult != null && addResult > 0) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "新增分类信息成功");
            }
        } else {
            turnMap.put(Constants.RESULT_MSG_STR, "新增分类失败，编码已存在");
        }

        return turnMap;
    }

    @Override
    public Map<String, Object> editExcType(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "修改分类信息失败");
        String oldStatusCd = MapUtils.getString(params, "oldStatusCd");
        String statusCd = MapUtils.getString(params, "statusCd");
        Long typeId = MapUtils.getLong(params, "typeId");
        CoExcType oldCoExcType = coExcTypeDao.selectByPrimaryKey(typeId);
        oldStatusCd = oldCoExcType.getStatusCd();
        Map<String,Object> paras = new HashMap<>();
        paras.put("oldStatusCd",oldCoExcType.getStatusCd());
        paras.put("statusCd",statusCd);
        //判断classCode是否存在
        Integer count = coExcTypeDao.selectCountByTypeCode(paras);
        if (count == 0) {
            CoExcType coExcType = new CoExcType();
            coExcType.setTypeId(typeId);
            coExcType.setTypeCode(MapUtils.getString(params, "typeCode"));
            coExcType.setTypeDesc(MapUtils.getString(params, "typeDesc"));
            coExcType.setSortSeq(MapUtils.getInteger(params, "sortSeq"));
            LocalDateTime nowLocalDateTime = LocalDateTime.now();
            coExcType.setUpdateDate(nowLocalDateTime);
            if (!StringUtils.isEmpty(statusCd) && !StringUtils.isEmpty(oldStatusCd) && !statusCd.equals(oldStatusCd)) {
                coExcType.setStatusCd(statusCd);
                coExcType.setStatusDate(LocalDateTime.now());
            }

            Integer addResult = this.coExcTypeDao.updateByPrimaryKeySelective(coExcType);

            if (addResult != null && addResult > 0) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "新增分类信息成功");
            }
        } else {
            turnMap.put(Constants.RESULT_MSG_STR, "新增分类失败，编码已存在");
        }


        return turnMap;
    }

    @Override
    public CoExcType selectByPrimaryKey(Long typeId) {
        return coExcTypeDao.selectByPrimaryKey(typeId);
    }
}
