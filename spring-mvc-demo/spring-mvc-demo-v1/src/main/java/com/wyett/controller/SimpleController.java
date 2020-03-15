package com.wyett.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : wyettLei
 * @date : Created in 2020/3/15 16:15
 * @description: TODO
 */

public class SimpleController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("/WEB-INF/page/userView.jsp");
        modelAndView.addObject("name", "wyett");
        return modelAndView;
    }
}
