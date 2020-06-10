package com.web.controller;

import com.web.bean.StaffInfo;
import com.web.bmo.CoProdClassBmo;
import com.web.common.BaseController;
import com.web.common.Constants;
import com.web.model.CoProdClass;
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
 * 商品类型
 * </p>
 *
 * @package: com.web.controller
 * @description: 商品类型
 * @author: wangzx8
 * @date: 2020/5/22 16:51
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Controller("com.web.controller.ProdClassController")
@RequestMapping(value = "/prodClass")
public class ProdClassController extends BaseController {

    @Resource(name = "com.web.bmo.CoProdClassBmoImpl")
    private CoProdClassBmo coProdClassBmo;

    @RequestMapping(value = "/listIndex")
    public String index(HttpServletRequest request, HttpServletResponse response, Model m) throws IOException {
        return "prod/class/prodClass";
    }

    /**
     * 进入产品分组新增和修改页面
     */
    @RequestMapping(value = "/toEditProdClass")
    public String toEditProdClass(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
            //操作类型，ADD和EDIT
            String editType = MapUtils.getString(params, "editType", "ADD");
            String classCode = MapUtils.getString(params, "classCode", null);


            //如果是修改，需要查询任务信息
            if ("EDIT".equals(editType)) {
                CoProdClass coProdClass = this.coProdClassBmo.selectByPrimaryKey(classCode);

                if (coProdClass == null) {
                    model.addAttribute("message", "未查询到任务信息");
                    return "/error/error";
                }

                model.addAttribute("classInfo", coProdClass);
            }

            model.addAttribute("editType", editType);
            model.addAttribute("oldClassCode", classCode);
        } catch (Exception e) {
            log.error("在跳转任务分组展示页面时获得异常 :", e);

            return "/error/error";
        }

        return "/prod/class/prodClassEdit";
    }

    /**
     * 抽奖活动列表查询
     *
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/classList", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> classList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            resultMap = coProdClassBmo.qryClassList(params);
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
    @RequestMapping(value = "/saveProdClass", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse saveProdClass(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");

        try {
            //获取前台传入的参数
            Map<String, Object> infoMap = new HashMap<String, Object>();
            String data = MapUtils.getString(params, "datas");

            if (!StringUtil.isEmptyStr(data)) {
                infoMap = listToMap(JsonUtil.toObject(data, ArrayList.class));
            }

            if (!StringUtil.isEmptyMap(infoMap)) {
                params.putAll(infoMap);
            }

            //获取登录数据信息
            StaffInfo staffInfo = (StaffInfo) session.getAttribute("staffInfo");

            params.put("staffId", staffInfo.getStaffId());

            //判断操作类型
            String editType = MapUtils.getString(params, "editType", "ADD");

            Map<String, Object> editResultMap = null;

            if ("ADD".equals(editType)) {
                editResultMap = this.coProdClassBmo.addProdClass(params);
            } else {
                editResultMap = this.coProdClassBmo.editProdClass(params);
            }

            if (!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))) {
                return super.failed(turnMap, 1);
            }

            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息成功");
            return super.successed(turnMap, 0);
        } catch (Exception e) {
            log.error("在保存任务分组信息时获得差异:", e);
            turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息异常：" + e);
        }

        return super.failed(turnMap, 1);
    }

    public static Map<String, Object> listToMap(List<Map<String, Object>> list) {
        Map<String, Object> turnMap = new HashMap<String, Object>();

        for (Map<String, Object> listInfo : list) {
            turnMap.put(MapUtils.getString(listInfo, "name"), listInfo.get("value"));
        }

        return turnMap;
    }

}
