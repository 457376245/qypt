package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.CoProductDao;
import com.web.dao.CoProductStockDao;
import com.web.dao.ImgDao;
import com.web.model.CoImg;
import com.web.model.CoProduct;
import com.web.model.CoProductStock;
import com.web.util.MapUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品
 * </p>
 *
 * @package: com.web.bmo
 * @description: 产品
 * @author: wangzx8
 * @date: 2020/5/23 14:47
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Service("com.web.bmo.CoProductBmoImpl")
public class CoProductBmoImpl implements CoProductBmo {

    @Autowired
    private CoProductDao coProductDao;

    @Autowired
    private ImgDao imgDao;

    @Autowired
    private CoProductStockDao coProductStockDao;

    @Override
    public Map<String, Object> qryProductList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(params, "page");
        int pageSize = MapUtil.asInt(params, "limit");
        PageHelper.startPage(pageNum, pageSize);
        List<CoProduct> list = coProductDao.queryProduct(params);
        PageInfo<CoProduct> info = new PageInfo<>(list);
        resultMap.put("data", info.getList());
        resultMap.put("count", info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> addProduct(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "新增产品信息失败");

        CoProduct coProduct = new CoProduct();
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //品牌编码
        String brandCode = MapUtils.getString(params, "brandCode");
        coProduct.setBrandCode(brandCode);
        //产品类编码
        String classCode = MapUtils.getString(params, "classCode");
        coProduct.setClassCode(classCode);
        //分组ID
        Long groupId = MapUtils.getLong(params, "groupId");
        coProduct.setGroupId(groupId);
        //产品编码
        String productCode = MapUtils.getString(params, "productCode");
        Map<String, Object> paramCoundMap = new HashMap<>();
        paramCoundMap.put("productCode", productCode);
        Integer count = coProductDao.selectByProductCodeCount(paramCoundMap);
        if (count > 0) {
            turnMap.put(Constants.RESULT_MSG_STR, "当前编码已经存在");
            return turnMap;
        }
        coProduct.setProductCode(productCode);
        //第三方编码
        String oProductCode = MapUtils.getString(params, "oProductCode");
        coProduct.setoProductCode(oProductCode);
        //提供平台 1  自有 2  第三方
        String prodSupplier = MapUtils.getString(params, "prodSupplier");
        coProduct.setProdSupplier(prodSupplier);
        //产品名称
        String productTitle = MapUtils.getString(params, "productTitle");
        coProduct.setProductTitle(productTitle);
        //副标题
        String productTitleSub = MapUtils.getString(params, "productTitleSub");
        coProduct.setProductTitleSub(productTitleSub);
        //属性标签
        String attrTab = MapUtils.getString(params, "attrTab");
        coProduct.setAttrTab(attrTab);
        //添加时间
        coProduct.setAddTime(nowLocalDateTime);
        //状态修改时间
        coProduct.setStatusDate(nowLocalDateTime);
        //修改时间
        coProduct.setUpdateDate(nowLocalDateTime);
        //状态 101 可用 102 不可用
        String statusCd = MapUtils.getString(params, "statusCd");
        coProduct.setStatusCd(statusCd);
        //显示日期
        String showTimeStr = MapUtils.getString(params, "showTime");
        LocalDateTime showTime = LocalDateTime.parse(showTimeStr, df);
        coProduct.setShowTime(showTime);
        //开始时间
        String startTimeStr = MapUtils.getString(params, "startTime");
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, df);
        coProduct.setStartTime(startTime);
        //结束时间
        String endTimeStr = MapUtils.getString(params, "endTime");
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, df);
        coProduct.setEndTime(endTime);
        //产品库存
        Integer productStock = MapUtils.getInteger(params, "productStock");
//        coProduct.setProductStock(productStock);
        //库存管理方式 1 自有 2 第三方
        String stockType = MapUtils.getString(params, "stockType");
        coProduct.setStockType(prodSupplier);
        //排序
        Integer sortSeq = MapUtils.getInteger(params, "sortSeq");
//        coProduct.setSortSeq(sortSeq);
        //产品类型
        String productType = MapUtils.getString(params, "productType");
        coProduct.setProductType(productType);
        //组合类型 1 单产品 2 组合产品
        String groupType = MapUtils.getString(params, "groupType");
        coProduct.setGroupType(groupType);
        //产品原价展示
        String oldPrice = MapUtils.getString(params, "oldPrice");
        coProduct.setOldPrice(oldPrice);
        //产品展示价
        String newPrice = MapUtils.getString(params, "newPrice");

        //新建工号
        Long staffId = MapUtils.getLong(params, "staffId");
        coProduct.setStaffId(staffId);
        //最后编辑工号
        coProduct.setEditStaffId(staffId);
        //产品销量
//        coProduct.setSellCount(0);
        //链接方式 1  本地详情 2  第三方详情
        coProduct.setLinkType("1");
        //平台编码
//        coProduct.setPlatformCode();
        //列表图片编码
        Long listImgCode = MapUtils.getLong(params, "detailImgeId");
        coProduct.setListImgCode("" + listImgCode);
        //图片编码
        Long imgCode = MapUtils.getLong(params, "thumbnailId");
        coProduct.setImgCode("" + imgCode);
        //版本号
        coProduct.setVersionNo(1L);


        String productDes = MapUtils.getString(params, "productDes");
        coProduct.setProductDes(productDes);

        String stockInst = MapUtils.getString(params, "stockInst");
        coProduct.setStockInst(stockInst);

        Integer addResult = this.coProductDao.insertSelective(coProduct);
        //修改图片
        CoImg listCoIma = new CoImg();
        listCoIma.setImgId(listImgCode);
        listCoIma.setLinkId(productCode);
        listCoIma.setLinkType("1");
        listCoIma.setFileType("1");
        imgDao.updateByPrimaryKeySelective(listCoIma);

        CoImg detailCoIma = new CoImg();
        detailCoIma.setImgId(imgCode);
        detailCoIma.setLinkId(productCode);
        detailCoIma.setLinkType("1");
        detailCoIma.setFileType("2");
        imgDao.updateByPrimaryKeySelective(detailCoIma);
        Integer stockCount = MapUtils.getInteger(params, "stockCount");//商品库存
        Integer productWarn = MapUtils.getInteger(params, "productWarn");//预警数量
        CoProductStock coProductStock = new CoProductStock();
        coProductStock.setProductId(coProduct.getProductId());
        coProductStock.setProductCode(coProduct.getProductCode());
        coProductStock.setProductStock(stockCount);
        coProductStock.setProductUse(0);
        coProductStock.setProductPreemption(0);
        coProductStock.setProductWarn(productWarn);
        coProductStock.setAddTime(nowLocalDateTime);
        coProductStock.setStatusDate(nowLocalDateTime);
        coProductStock.setUpdateDate(nowLocalDateTime);
        coProductStock.setStatusCd(statusCd);
        coProductStockDao.insertSelective(coProductStock);

        if (addResult != null && addResult > 0) {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "新增产品信息成功");
        }

        return turnMap;
    }

    @Override
    public Map<String, Object> editProduct(Map<String, Object> params) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "修改产品信息失败");
        Long productId = MapUtils.getLong(params, "productId");

        CoProduct oldCoProduct = coProductDao.selectByPrimaryKey(productId);
        CoProduct coProduct = new CoProduct();
        coProduct.setProductId(productId);
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //品牌编码
        String brandCode = MapUtils.getString(params, "brandCode");
        coProduct.setBrandCode(brandCode);
        //产品类编码
        String classCode = MapUtils.getString(params, "classCode");
        coProduct.setClassCode(classCode);
        //分组ID
        Long groupId = MapUtils.getLong(params, "groupId");
        coProduct.setGroupId(groupId);
        //产品编码
        String productCode = MapUtils.getString(params, "productCode");
        if (!productCode.equals(oldCoProduct.getProductCode())) {
            Map<String, Object> paramCoundMap = new HashMap<>();
            paramCoundMap.put("productCode", productCode);
            paramCoundMap.put("oldProductCode", oldCoProduct.getProductCode());
            Integer count = coProductDao.selectByProductCodeCount(paramCoundMap);
            if (count > 0) {
                turnMap.put(Constants.RESULT_MSG_STR, "当前编码已经存在");
                return turnMap;
            }
        }
        coProduct.setProductCode(productCode);
        //第三方编码
        String oProductCode = MapUtils.getString(params, "oProductCode");
        coProduct.setoProductCode(oProductCode);
        //提供平台 1  自有 2  第三方
        String prodSupplier = MapUtils.getString(params, "prodSupplier");
        coProduct.setProdSupplier(prodSupplier);
        //产品名称
        String productTitle = MapUtils.getString(params, "productTitle");
        coProduct.setProductTitle(productTitle);
        //副标题
        String productTitleSub = MapUtils.getString(params, "productTitleSub");
        coProduct.setProductTitleSub(productTitleSub);
        //属性标签
        String attrTab = MapUtils.getString(params, "attrTab");
        coProduct.setAttrTab(attrTab);

        //修改时间
        coProduct.setUpdateDate(nowLocalDateTime);

        String statusCd = MapUtils.getString(params, "statusCd");
        if (!statusCd.equals(oldCoProduct.getStatusCd())) {
            //状态 101 可用 102 不可用
            coProduct.setStatusCd(statusCd);
            //状态修改时间
            coProduct.setStatusDate(nowLocalDateTime);
        }
        //显示日期
        String showTimeStr = MapUtils.getString(params, "showTime");
        LocalDateTime showTime = LocalDateTime.parse(showTimeStr, df);
        coProduct.setShowTime(showTime);
        //开始时间
        String startTimeStr = MapUtils.getString(params, "startTime");
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, df);
        coProduct.setStartTime(startTime);
        //结束时间
        String endTimeStr = MapUtils.getString(params, "endTime");
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, df);
        coProduct.setEndTime(endTime);
        //库存管理方式 1 自有 2 第三方
        coProduct.setStockType(prodSupplier);
        //产品类型
        String productType = MapUtils.getString(params, "productType");
        coProduct.setProductType(productType);
        //组合类型 1 单产品 2 组合产品
        String groupType = MapUtils.getString(params, "groupType");
        coProduct.setGroupType(groupType);
        //产品原价展示
        String oldPrice = MapUtils.getString(params, "oldPrice");
        coProduct.setOldPrice(oldPrice);
        //产品展示价
        String newPrice = MapUtils.getString(params, "newPrice");

        //新建工号
        Long staffId = MapUtils.getLong(params, "staffId");
        coProduct.setStaffId(staffId);
        //最后编辑工号
        coProduct.setEditStaffId(staffId);
        //链接方式 1  本地详情 2  第三方详情
        coProduct.setLinkType("1");
        //列表图片编码
        Long listImgCode = MapUtils.getLong(params, "detailImgeId", 0L);
        coProduct.setListImgCode("" + listImgCode);
        //图片编码
        Long imgCode = MapUtils.getLong(params, "thumbnailId", 0L);
        coProduct.setImgCode("" + imgCode);
        //版本号
        coProduct.setVersionNo(1L);


        String productDes = MapUtils.getString(params, "productDes");
        coProduct.setProductDes(productDes);

        String stockInst = MapUtils.getString(params, "stockInst");
        coProduct.setStockInst(stockInst);

        Integer editResult = this.coProductDao.updateByPrimaryKeySelective(coProduct);
        if (editResult != null && editResult > 0) {
            if (listImgCode != 0L) {
                String listImgCodeStr = "" + listImgCode;
                if (!listImgCodeStr.equals(oldCoProduct.getListImgCode())) {
                    //修改图片
                    CoImg listCoIma = new CoImg();
                    listCoIma.setImgId(listImgCode);
                    listCoIma.setLinkId(productCode);
                    listCoIma.setLinkType("1");
                    listCoIma.setFileType("1");
                    imgDao.updateByPrimaryKeySelective(listCoIma);
                }
            }
            if (imgCode != 0L) {
                String imgCodeStr = "" + imgCode;
                if (!imgCodeStr.equals(oldCoProduct.getImgCode())) {
                    CoImg detailCoIma = new CoImg();
                    detailCoIma.setImgId(imgCode);
                    detailCoIma.setLinkId(productCode);
                    detailCoIma.setLinkType("1");
                    detailCoIma.setFileType("2");
                    imgDao.updateByPrimaryKeySelective(detailCoIma);
                }
            }
        }

        CoProductStock oldCoProductStock = coProductStockDao.selectByPrimaryKey(productId);
        Integer stockCount = MapUtils.getInteger(params, "stockCount");//商品库存
        Integer productWarn = MapUtils.getInteger(params, "productWarn");//预警数量
        if(!oldCoProductStock.getProductStock().equals(stockCount) || !oldCoProductStock.getProductWarn().equals(productWarn)) {
            CoProductStock coProductStock = new CoProductStock();
            coProductStock.setProductId(coProduct.getProductId());
            if(!oldCoProductStock.getProductStock().equals(stockCount)) {
                coProductStock.setProductStock(stockCount);
            }
            if(!oldCoProductStock.getProductWarn().equals(productWarn)) {
                coProductStock.setProductWarn(productWarn);
            }
            coProductStock.setUpdateDate(nowLocalDateTime);
            coProductStockDao.updateByPrimaryKeySelective(coProductStock);
        }
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
        turnMap.put(Constants.RESULT_MSG_STR, "修改产品信息成功");
        return turnMap;
    }


    @Override
    public CoProduct selectByPrimaryKey(Long productId) {
        return coProductDao.selectByPrimaryKey(productId);
    }


}
