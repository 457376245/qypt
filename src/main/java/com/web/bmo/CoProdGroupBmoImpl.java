package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.CoProdGroupDao;
import com.web.model.CoProdGroup;
import com.web.util.MapUtil;
import com.web.util.Toolkit;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品分组
 * </p>
 *
 * @package: com.web.bmo
 * @description: 商品分组
 * @author: wangzx8
 * @date: 2020/5/20 17:13
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Service("com.web.bmo.CoProdGroupBmoImpl")
public class CoProdGroupBmoImpl implements CoProdGroupBmo {

    @Autowired
    private CoProdGroupDao coProdGroupDao;


    @Override
    public int deleteByPrimaryKey(Long groupId) {

//        return coProdGroupDao.deleteByPrimaryKey(groupId);
        CoProdGroup coProdGroup = new CoProdGroup();
        coProdGroup.setGroupId(groupId);
        coProdGroup.setStatusCd("102");
        return coProdGroupDao.updateByPrimaryKeySelective(coProdGroup);

    }

    @Override
    public int insert(CoProdGroup record) {
        return coProdGroupDao.insert(record);
    }

    @Override
    public int insertSelective(CoProdGroup record) {
        return coProdGroupDao.insertSelective(record);
    }

    @Override
    public CoProdGroup selectByPrimaryKey(Long groupId) {
        return coProdGroupDao.selectByPrimaryKey(groupId);
    }

    @Override
    public int updateByPrimaryKeySelective(CoProdGroup record) {
        return coProdGroupDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(CoProdGroup record) {
        return coProdGroupDao.updateByPrimaryKey(record);
    }

    @Override
    public int batchInsert(List<CoProdGroup> list) {
        return coProdGroupDao.batchInsert(list);
    }

    @Override
    public Map<String, Object> qryGroupList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(params, "page");
        int pageSize = MapUtil.asInt(params, "limit");

        PageHelper.startPage(pageNum, pageSize);
        List<CoProdGroup> list = coProdGroupDao.queryGroup(params);
        PageInfo<CoProdGroup> info = new PageInfo<>(list);
        resultMap.put("data",info.getList());
        resultMap.put("count",info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> addProdGroup(Map<String, Object> params) {
        Map<String, Object> turnMap=new HashMap<String,Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "新增分组信息失败");

        CoProdGroup coProdGroup = new CoProdGroup();
        coProdGroup.setBrandTitle(MapUtils.getString(params,"brandTitle"));
        coProdGroup.setBrandDesc(MapUtils.getString(params,"brandDesc"));
        coProdGroup.setSortSeq(MapUtils.getInteger(params,"sortSeq"));
        coProdGroup.setAddTime(LocalDateTime.now());
        coProdGroup.setStatusDate(LocalDateTime.now());
        coProdGroup.setUpdateDate(LocalDateTime.now());
        coProdGroup.setStatusCd(MapUtils.getString(params,"statusCd"));
        coProdGroup.setStaffId(MapUtils.getLong(params,"staffId"));
        coProdGroup.setEditStaffId(MapUtils.getLong(params,"staffId"));
        Integer addResult=this.coProdGroupDao.insertSelective(coProdGroup);

        if(addResult!=null && addResult>0){
            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "新增分组信息成功");
        }

        return turnMap;
    }

    @Override
    public Map<String, Object> editProdGroup(Map<String, Object> params) {
        Map<String, Object> turnMap=new HashMap<String,Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "修改分组信息失败");
        String oldStatusCd = MapUtils.getString(params,"oldStatusCd");
        String statusCd = MapUtils.getString(params,"statusCd");
        CoProdGroup coProdGroup = new CoProdGroup();
        coProdGroup.setGroupId(MapUtils.getLong(params,"groupId"));
        coProdGroup.setBrandTitle(MapUtils.getString(params,"brandTitle"));
        coProdGroup.setBrandDesc(MapUtils.getString(params,"brandDesc"));
        coProdGroup.setSortSeq(MapUtils.getInteger(params,"sortSeq"));
        if(!StringUtils.isEmpty(statusCd) && !StringUtils.isEmpty(oldStatusCd) && !statusCd.equals(oldStatusCd)) {
            coProdGroup.setStatusDate(LocalDateTime.now());
        }
        coProdGroup.setStatusCd(MapUtils.getString(params,"statusCd"));
        coProdGroup.setUpdateDate(LocalDateTime.now());
        coProdGroup.setEditStaffId( MapUtils.getLong(params,"staffId"));
        Integer addResult=this.coProdGroupDao.updateByPrimaryKeySelective(coProdGroup);

        if(addResult!=null && addResult>0){
            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "新增分组信息成功");
        }

        return turnMap;
    }
}
