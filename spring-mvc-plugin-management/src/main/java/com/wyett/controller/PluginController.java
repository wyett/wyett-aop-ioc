package com.wyett.controller;

import com.wyett.spring.DefaultSpringPluginFactory;
import com.wyett.spring.PluginConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/29 18:28
 * @description: TODO
 */

@Controller
@RequestMapping(value = "/plugin")
public class PluginController {

    @Autowired
    private DefaultSpringPluginFactory pluginFactory;

    //获取插件配置列表
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getHavePlugins(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Collection<PluginConfig> lst = null;
        lst = pluginFactory.flushConfigs();
        req.setAttribute("havePlugins", lst);
        req.getRequestDispatcher("/plugins.jsp").forward(req, res);
        return "/plugins";

    }

    //启用指定插件
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public void activePlugin(HttpServletRequest req, HttpServletResponse res) {
        pluginFactory.activePlugin(req.getParameter("id"));
        try {
            res.getWriter().append("active succeed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //启用指定插件
    @RequestMapping(value = "/disable", method = RequestMethod.GET)
    public void disablePlugin(HttpServletRequest req, HttpServletResponse res) {
        pluginFactory.disablePlugin(req.getParameter("id"));
        try {
            res.getWriter().append("active succeed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
