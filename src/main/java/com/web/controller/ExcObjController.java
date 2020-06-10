package com.web.controller;

import com.web.bean.StaffInfo;
import com.web.bmo.CoExcObjBmo;
import com.web.bmo.CoExcTypeBmo;
import com.web.bmo.CoProductBmo;
import com.web.common.BaseController;
import com.web.common.Constants;
import com.web.model.CoExcObj;
import com.web.model.CoExcType;
import com.web.model.CoProduct;
import com.web.util.JsonUtil;
import com.web.util.StringUtil;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品
 * </p>
 *
 * @package: com.web.controller
 * @description: 商品
 * @author: wangzx8
 * @date: 2020/6/1 11:22
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Controller("com.web.controller.ExcObjController")
@RequestMapping(value = "/excObj")
public class ExcObjController extends BaseController {

    @Resource(name = "com.web.bmo.CoExcObjBmoImpl")
    private CoExcObjBmo coExcObjBmo;

    @Resource(name = "com.web.bmo.CoProductBmoImpl")
    private CoProductBmo coProductBmo;

    @RequestMapping(value = "/listIndex")
    public String index(HttpServletRequest request, HttpServletResponse response, Model m) throws IOException {
        return "prod/excObj/excObj";
    }

    /**
     * 进入产品分组新增和修改页面
     */
    @RequestMapping(value = "/toEditExcObj")
    public String toEditExcObj(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
            //操作类型，ADD和EDIT
            String editType = MapUtils.getString(params, "editType", "ADD");
            Long objId = MapUtils.getLong(params, "objId", null);
            //如果是修改，需要查询任务信息
            if ("EDIT".equals(editType)) {
                CoExcObj coExcObj = this.coExcObjBmo.selectByPrimaryKey(objId);

                if (coExcObj == null) {
                    model.addAttribute("message", "未查询到任务信息");
                    return "/error/error";
                }
                CoProduct coProduct = coProductBmo.selectByPrimaryKey(coExcObj.getProductId());
                model.addAttribute("oldStatusCd", coExcObj.getStatusCd());
                model.addAttribute("excObjInfo", coExcObj);
                model.addAttribute("oldStatusCd", coExcObj.getStatusCd());
                model.addAttribute("oldObjCode", coExcObj.getObjCode());
                model.addAttribute("productTitle", coProduct.getProductTitle() + "(" + coProduct.getProductCode() + ")");
            }

            model.addAttribute("editType", editType);
            model.addAttribute("objId", objId);

        } catch (Exception e) {
            log.error("在跳转任务分组展示页面时获得异常 :", e);

            return "/error/error";
        }

        return "/prod/excObj/excObjEdit";
    }

    /**
     * 抽奖活动列表查询
     *
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/objList", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> objList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            resultMap = coExcObjBmo.qryExcObjList(params);
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

    /**保存权益分组信息（新增和修改）*/
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/saveExcObj",method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse saveExcType(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        Map<String, Object> turnMap=new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");
        try {
            //获取登录数据信息
            StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
            params.put("staffId", staffInfo.getStaffId());
            //判断操作类型
            String editType= MapUtils.getString(params, "editType","ADD");
            Map<String, Object> editResultMap=null;
            if("ADD".equals(editType)){
                editResultMap=this.coExcObjBmo.addExcObj(params);
            }else{
                editResultMap=this.coExcObjBmo.editExcObj(params);
            }
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))){
                return super.failed(editResultMap, 1);
            }
            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息成功");
            return super.successed(turnMap, 0);
        } catch (Exception e) {
            log.error("在保存任务分组信息时获得差异:",e);

            turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息异常："+e);
        }
        return super.failed(turnMap,1);
    }

    /**
     * 打开产品列表
     */
    @RequestMapping(value = "/toProduct")
    public String toProduct(@RequestParam Map<String, Object> params,Model model) {
        Long productId = MapUtils.getLong(params,"productId");
        if(productId != null) {
            model.addAttribute("oldProductId", productId);
        }
        return "/prod/excObj/product";
    }

    /**
     * 返回产品列表
     */
    @RequestMapping(value = "/productList",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object>  productList(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            resultMap = coExcObjBmo.qryProductList(params);
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


}
