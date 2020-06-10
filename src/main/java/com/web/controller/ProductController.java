package com.web.controller;

import com.web.bean.StaffInfo;
import com.web.bmo.CoProductBmo;
import com.web.bmo.CoProductStockBmo;
import com.web.bmo.CommonBmo;
import com.web.bmo.ImgBmo;
import com.web.common.BaseController;
import com.web.common.Constants;
import com.web.model.CoImg;
import com.web.model.CoProduct;
import com.web.model.CoProductStock;
import com.web.util.RedisUtil;
import com.web.util.common.JsonResponse;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 产品信息
 * </p>
 *
 * @package: com.web.controller
 * @description: 产品信息
 * @author: wangzx8
 * @date: 2020/5/27 9:42
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Controller("com.web.controller.ProductController")
@RequestMapping(value = "/product")
public class ProductController extends BaseController {
    @Resource(name = "com.web.bmo.CoProductBmoImpl")
    private CoProductBmo coProductBmo;

    @Resource(name = "RedisUtil")
    private RedisUtil redisUtil;

    @Resource(name = "com.web.bmo.ImgBmoImpl")
    private ImgBmo imgBmo;

    @Resource
    private CommonBmo commonBmo;

    @Resource
    private CoProductStockBmo coProductStockBmo;

    @RequestMapping(value = "/listIndex")
    public String index(HttpServletRequest request, HttpServletResponse response, Model m) throws IOException {
        return "prod/product/product";
    }

    /**
     * 进入产品分组新增和修改页面
     */
    @RequestMapping(value = "/toEditProduct")
    public String toEditProduct(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
            //操作类型，ADD和EDIT
            String editType = MapUtils.getString(params, "editType", "ADD");
            Long productId = MapUtils.getLong(params, "productId", null);

            //如果是修改，需要查询任务信息
            if ("EDIT".equals(editType)) {
                CoProduct coProduct = this.coProductBmo.selectByPrimaryKey(productId);
                if (coProduct == null) {
                    model.addAttribute("message", "未查询到任务信息");
                    return "/error/error";
                }
                model.addAttribute("oldStatusCd", coProduct.getStatusCd());
                model.addAttribute("productInfo", coProduct);

                //获取图片
                CoImg record = new CoImg();
                record.setLinkId(coProduct.getProductCode());
                record.setStatusCd("101");
                List<CoImg> coImgList = imgBmo.selectSelective(record);
                String imageUrl = commonBmo.getImageServerUrl();
                for (CoImg img : coImgList) {
                    String fileType = img.getFileType();
                    if ("1".equals(fileType)) {//列表图片
                        Map<String, Object> imgMap = new HashMap<>();
                        imgMap.put("src", imageUrl + img.getFileDir() + img.getNewName());
                        imgMap.put("imgId", img.getImgId());
                        model.addAttribute("listImg", imgMap);
                    } else if ("2".equals(fileType)) {//详情图片
                        Map<String, Object> imgMap = new HashMap<>();
                        imgMap.put("src", imageUrl + img.getFileDir() + img.getNewName());
                        imgMap.put("imgId", img.getImgId());
                        model.addAttribute("detailImg", imgMap);
                    }
                }
                //获取库存
                CoProductStock coProductStock = coProductStockBmo.selectByPrimaryKey(productId);
                model.addAttribute("productStock", coProductStock);

            } else {
                LocalDateTime nowLocalDateTime = LocalDateTime.now();
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String localTime = df.format(nowLocalDateTime);
                String incr = redisUtil.incrAtomic(Constants.REDIS_PRODUCT_NBR + localTime, 1, 6, TimeUnit.SECONDS);
                String genProdCode = localTime + incr;
                model.addAttribute("genProdCode", genProdCode);
            }

            model.addAttribute("editType", editType);
            model.addAttribute("productId", productId);

        } catch (Exception e) {
            log.error("在跳转任务分组展示页面时获得异常 :", e);

            return "/error/error";
        }

        return "/prod/product/productEdit";
    }

    /**
     * 抽奖活动列表查询
     *
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/productList", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> productList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            resultMap = coProductBmo.qryProductList(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        } catch (Exception e) {
            resultMap.put("code", -1);
            resultMap.put("msg", "抽奖活动列表查询异常");
            log.error("抽奖活动列表查询异常:", e);
        }
        return resultMap;
    }

    /**
     * 保存权益分组信息（新增和修改）
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/saveProduct", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse saveProduct(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");

        try {
            //获取登录数据信息
            StaffInfo staffInfo = (StaffInfo) session.getAttribute("staffInfo");
            params.put("staffId", staffInfo.getStaffId());
            //判断操作类型
            String editType = MapUtils.getString(params, "editType", "ADD");
            Map<String, Object> editResultMap = null;
            if ("ADD".equals(editType)) {
                editResultMap = this.coProductBmo.addProduct(params);
            } else {
                editResultMap = this.coProductBmo.editProduct(params);
            }
            if (!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))) {
                return super.failed(editResultMap, 1);
            }
            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息成功");
            return super.successed(turnMap, 0);
        } catch (Exception e) {
            log.error("在保存产品信息时获得差异:", e);

            turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息异常：" + e);
        }

        return super.failed(turnMap, 1);
    }


    /**
     * 保存权益分组信息（新增和修改）
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/addProductStock", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse addProductStock(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "保存库存失败");
        try {
            //获取登录数据信息
            StaffInfo staffInfo = (StaffInfo) session.getAttribute("staffInfo");
            params.put("staffId", staffInfo.getStaffId());
            Map<String, Object> editResultMap = coProductStockBmo.addProductStock(params);
            if (!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))) {
                return super.failed(editResultMap, 1);
            }
            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息库存成功");
            return super.successed(turnMap, 0);
        } catch (Exception e) {
            log.error("在保存库存时获得差异:", e);
            turnMap.put(Constants.RESULT_MSG_STR, "保存库存异常：" + e);
        }
        return super.failed(turnMap, 1);
    }


}
