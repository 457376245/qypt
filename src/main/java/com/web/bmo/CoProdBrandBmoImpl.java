package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.CoProdBrandDao;
import com.web.model.CoProdBrand;
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
 * 商品品牌
 * </p>
 *
 * @package: com.web.bmo
 * @description: 商品品牌
 * @author: wangzx8
 * @date: 2020/5/22 19:13
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Service("com.web.bmo.CoProdBrandBmoImpl")
public class CoProdBrandBmoImpl implements CoProdBrandBmo{

    @Autowired
    private CoProdBrandDao coProdBrandDao;


    @Override
    public Map<String, Object> qryBrandList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(params, "page");
        int pageSize = MapUtil.asInt(params, "limit");

        PageHelper.startPage(pageNum, pageSize);
        List<CoProdBrand> list = coProdBrandDao.queryBrand(params);
        PageInfo<CoProdBrand> info = new PageInfo<>(list);
        resultMap.put("data", info.getList());
        resultMap.put("count", info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> addProdBrand(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "新增分类信息失败");

        //判断classCode是否存在
        Long count = coProdBrandDao.selectCountByBrandCode(params);
        if (count == 0) {
            CoProdBrand coProdBrand = new CoProdBrand();
            coProdBrand.setBrandCode(MapUtils.getString(params, "brandCode"));
            coProdBrand.setBrandTitle(MapUtils.getString(params, "brandTitle"));
            coProdBrand.setBrandDesc(MapUtils.getString(params, "brandDesc"));
            coProdBrand.setSortSeq(MapUtils.getInteger(params, "sortSeq"));
            coProdBrand.setAddTime(LocalDateTime.now());
            coProdBrand.setStatusDate(LocalDateTime.now());
            coProdBrand.setStatusDate(LocalDateTime.now());
            coProdBrand.setUpdateDate(LocalDateTime.now());
            coProdBrand.setStatusCd(MapUtils.getString(params, "statusCd"));
            coProdBrand.setStaffId(MapUtils.getLong(params, "staffId"));
            coProdBrand.setEditStaffId(MapUtils.getLong(params, "staffId"));
            Integer addResult = this.coProdBrandDao.insertSelective(coProdBrand);

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
    public Map<String, Object> editProdBrand(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "修改分组信息失败");
        String brandCode = MapUtils.getString(params, "brandCode");
        String oldBrandCode = MapUtils.getString(params, "oldBrandCode");
        Long count = 1L;
        if (oldBrandCode.equals(brandCode)) {
            count = 0L;
        } else {
            count = coProdBrandDao.selectCountByBrandCode(params);
        }
        if (count == 0) {
            CoProdBrand coProdBrand = new CoProdBrand();
            coProdBrand.setBrandCode(MapUtils.getString(params, "brandCode"));
            coProdBrand.setBrandTitle(MapUtils.getString(params, "brandTitle"));
            coProdBrand.setBrandDesc(MapUtils.getString(params, "brandDesc"));
            coProdBrand.setSortSeq(MapUtils.getInteger(params, "sortSeq"));
            coProdBrand.setStatusDate(LocalDateTime.now());
            coProdBrand.setStatusCd(MapUtils.getString(params, "statusCd"));
            coProdBrand.setUpdateDate(LocalDateTime.now());
            coProdBrand.setEditStaffId(MapUtils.getLong(params, "staffId"));
            coProdBrand.setOldBrandCode(MapUtils.getString(params, "oldBrandCode"));
            Integer addResult = this.coProdBrandDao.updateByPrimaryKeySelective(coProdBrand);

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
    public CoProdBrand selectByPrimaryKey(String brandCode) {
        CoProdBrand coProdBrand = coProdBrandDao.selectByPrimaryKey(brandCode);
        if (coProdBrand != null) {
            coProdBrand.setBrandCode(coProdBrand.getBrandCode());
        }
        return coProdBrand;
    }
}
