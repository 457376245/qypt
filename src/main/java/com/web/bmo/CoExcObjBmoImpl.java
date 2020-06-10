package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.CoExcObjDao;
import com.web.dao.CoExcTypeDao;
import com.web.dao.CoProductDao;
import com.web.model.CoExcObj;
import com.web.model.CoExcType;
import com.web.model.CoProduct;
import com.web.util.MapUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
 * @date: 2020/6/1 11:19
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Service("com.web.bmo.CoExcObjBmoImpl")
public class CoExcObjBmoImpl implements CoExcObjBmo {

    @Autowired
    private CoExcObjDao coExcObjDao;

    @Autowired
    private CoProductDao coProductDao;


    @Override
    public int deleteByPrimaryKey(Long objId) {
        return coExcObjDao.deleteByPrimaryKey(objId);
    }

    @Override
    public int insertSelective(CoExcObj record) {
        return coExcObjDao.insertSelective(record);
    }

    @Override
    public CoExcObj selectByPrimaryKey(Long objId) {
        return coExcObjDao.selectByPrimaryKey(objId);
    }

    @Override
    public int updateByPrimaryKeySelective(CoExcObj record) {
        return coExcObjDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<String, Object> qryExcObjList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(params, "page");
        int pageSize = MapUtil.asInt(params, "limit");
        PageHelper.startPage(pageNum, pageSize);
        List<CoExcObj> list = coExcObjDao.queryExcObj(params);
        PageInfo<CoExcObj> info = new PageInfo<>(list);
        resultMap.put("data", info.getList());
        resultMap.put("count", info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> addExcObj(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "新增商品信息失败");
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        //判断classCode是否存在
        Integer count = coExcObjDao.selectCountObjCode(params);
        if (count == 0) {
            CoExcObj coExcObj = new CoExcObj();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            coExcObj.setObjCode(MapUtils.getString(params, "objCode"));
            coExcObj.setProductId(MapUtils.getLong(params, "productId"));
            String payType = MapUtils.getString(params, "payType");
            coExcObj.setPayType(payType);
            Integer payVal = MapUtils.getInteger(params,"payVal");
            coExcObj.setPayVal(payVal);
            Integer payMoneyStr = MapUtils.getInteger(params,"payMoney");
            coExcObj.setPayMoney(payMoneyStr);
            coExcObj.setAddTime(nowLocalDateTime);
            String effDateStr = MapUtils.getString(params, "effDate");
            LocalDateTime effDate = LocalDateTime.parse(effDateStr, df);
            coExcObj.setEffDate(effDate);
            String expDateStr = MapUtils.getString(params, "expDate");
            LocalDateTime expDate = LocalDateTime.parse(expDateStr, df);
            coExcObj.setExpDate(expDate);
            coExcObj.setUpdateDate(nowLocalDateTime);
            Long staffId = MapUtils.getLong(params, "staffId");
            coExcObj.setStaffId(staffId);
            coExcObj.setEditStaffId(staffId);
            coExcObj.setSortSeq(MapUtils.getInteger(params, "sortSeq"));
            coExcObj.setStatusCd(MapUtils.getString(params, "statusCd"));
            Integer addResult = this.coExcObjDao.insertSelective(coExcObj);
            if (addResult != null && addResult > 0) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "新增商品信息成功");
            }
        } else {
            turnMap.put(Constants.RESULT_MSG_STR, "新增商品失败，编码已存在");
        }

        return turnMap;
    }

    @Override
    public Map<String, Object> editExcObj(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "修改分类信息失败");
        String oldStatusCd = MapUtils.getString(params, "oldStatusCd");
        String statusCd = MapUtils.getString(params, "statusCd");
        Long objId = MapUtils.getLong(params, "objId");
        CoExcObj oldCoExcObj = coExcObjDao.selectByPrimaryKey(objId);
        oldStatusCd = oldCoExcObj.getStatusCd();
        Map<String,Object> paras = new HashMap<>();
        paras.put("oldStatusCd",oldCoExcObj.getStatusCd());
        paras.put("statusCd",statusCd);
        //判断classCode是否存在
        Integer count = coExcObjDao.selectCountObjCode(paras);
        if (count == 0) {
            CoExcObj coExcObj = new CoExcObj();
            coExcObj.setObjId(objId);
            coExcObj.setSortSeq(MapUtils.getInteger(params, "sortSeq"));
            coExcObj.setObjCode(MapUtils.getString(params, "objCode"));
            coExcObj.setProductId(MapUtils.getLong(params, "productId"));
            String payType = MapUtils.getString(params, "payType");
            coExcObj.setPayType(payType);
            Integer payVal = MapUtils.getInteger(params,"payVal");
            coExcObj.setPayVal(payVal);
            Integer payMoneyStr = MapUtils.getInteger(params,"payMoney");
            coExcObj.setPayMoney(payMoneyStr);

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String effDateStr = MapUtils.getString(params, "effDate");
            LocalDateTime effDate = LocalDateTime.parse(effDateStr, df);
            coExcObj.setEffDate(effDate);
            String expDateStr = MapUtils.getString(params, "expDate");
            LocalDateTime expDate = LocalDateTime.parse(expDateStr, df);
            coExcObj.setExpDate(expDate);
            Long staffId = MapUtils.getLong(params, "staffId");
            coExcObj.setEditStaffId(staffId);
            LocalDateTime nowLocalDateTime = LocalDateTime.now();
            coExcObj.setUpdateDate(nowLocalDateTime);
            coExcObj.setSortSeq(MapUtils.getInteger(params, "sortSeq"));
            coExcObj.setStatusCd(MapUtils.getString(params, "statusCd"));

            Integer addResult = this.coExcObjDao.updateByPrimaryKeySelective(oldCoExcObj);

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
    public Map<String, Object> qryProductList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(params, "page");
        int pageSize = MapUtil.asInt(params, "limit");
        PageHelper.startPage(pageNum, pageSize);
        params.put("status_cd","101");
        params.put("isCommodity","1");
        List<CoProduct> list = coProductDao.queryProduct(params);
        PageInfo<CoProduct> info = new PageInfo<>(list);
        resultMap.put("data", info.getList());
        resultMap.put("count", info.getTotal());
        return resultMap;
    }
}
