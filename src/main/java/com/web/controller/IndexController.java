package com.web.controller;

import com.web.common.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 首页
 * </p>
 *
 * @package: com.web.controller
 * @description: 首页
 * @author: wangzx8
 * @date: 2020/6/4 16:17
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */

@Controller("com.web.controller.IndexController")
@RequestMapping(value = "/index")
public class IndexController extends BaseController {

    @RequestMapping(value = "")
    public String index(HttpServletRequest request, HttpServletResponse response, Model m) throws IOException {
        return "login/index";
    }
}
