package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.CoProdClassDao;
import com.web.model.CoProdClass;
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
@Service("com.web.bmo.CoProdClassBmoImpl")
public class CoProdClassBmoImpl implements CoProdClassBmo {

    @Autowired
    private CoProdClassDao coProdClassDao;


    @Override
    public Map<String, Object> qryClassList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(params, "page");
        int pageSize = MapUtil.asInt(params, "limit");

        PageHelper.startPage(pageNum, pageSize);
        List<CoProdClass> list = coProdClassDao.queryClass(params);
        PageInfo<CoProdClass> info = new PageInfo<>(list);
        resultMap.put("data", info.getList());
        resultMap.put("count", info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> addProdClass(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "新增分类信息失败");

        //判断classCode是否存在
        Long count = coProdClassDao.selectCountByClassCode(params);
        if (count == 0) {
            CoProdClass coProdClass = new CoProdClass();
            coProdClass.setClassCode(MapUtils.getString(params, "classCode"));
            coProdClass.setBrandTitle(MapUtils.getString(params, "brandTitle"));
            coProdClass.setBrandDesc(MapUtils.getString(params, "brandDesc"));
            coProdClass.setSortSeq(MapUtils.getInteger(params, "sortSeq"));
            coProdClass.setAddTime(LocalDateTime.now());
            coProdClass.setStatusDate(LocalDateTime.now());
            coProdClass.setStatusDate(LocalDateTime.now());
            coProdClass.setUpdateDate(LocalDateTime.now());
            coProdClass.setStatusCd(MapUtils.getString(params, "statusCd"));
            coProdClass.setStaffId(MapUtils.getLong(params, "staffId"));
            coProdClass.setEditStaffId(MapUtils.getLong(params, "staffId"));
            Integer addResult = this.coProdClassDao.insertSelective(coProdClass);

            if (addResult != null && addResult > 0) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "新增分组信息成功");
            }
        } else {
            turnMap.put(Constants.RESULT_MSG_STR, "新增分组失败，编码已存在");
        }

        return turnMap;
    }

    @Override
    public Map<String, Object> editProdClass(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "修改分组信息失败");
        String classCode = MapUtils.getString(params, "classCode");
        String oldClassCode = MapUtils.getString(params, "oldClassCode");
        Long count = 1L;
        if (oldClassCode.equals(classCode)) {
            count = 0L;
        } else {
            count = coProdClassDao.selectCountByClassCode(params);
        }
        if (count == 0) {
            CoProdClass coProdClass = new CoProdClass();
            coProdClass.setClassCode(MapUtils.getString(params, "classCode"));
            coProdClass.setBrandTitle(MapUtils.getString(params, "brandTitle"));
            coProdClass.setBrandDesc(MapUtils.getString(params, "brandDesc"));
            coProdClass.setSortSeq(MapUtils.getInteger(params, "sortSeq"));
            coProdClass.setStatusDate(LocalDateTime.now());
            coProdClass.setStatusCd(MapUtils.getString(params, "statusCd"));
            coProdClass.setUpdateDate(LocalDateTime.now());
            coProdClass.setEditStaffId(MapUtils.getLong(params, "staffId"));
            coProdClass.setOldClassCode(MapUtils.getString(params, "oldClassCode"));
            Integer addResult = this.coProdClassDao.updateByPrimaryKeySelective(coProdClass);

            if (addResult != null && addResult > 0) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "新增分组信息成功");
            }
        } else {
            turnMap.put(Constants.RESULT_MSG_STR, "新增分组失败，编码已存在");
        }
        return turnMap;
    }

    @Override
    public CoProdClass selectByPrimaryKey(String classCode) {
        CoProdClass coProdClass = coProdClassDao.selectByPrimaryKey(classCode);
        if (coProdClass != null) {
            coProdClass.setOldClassCode(coProdClass.getClassCode());
        }
        return coProdClass;
    }
}
