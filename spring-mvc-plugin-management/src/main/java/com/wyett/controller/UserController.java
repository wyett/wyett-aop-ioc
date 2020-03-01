package com.wyett.controller;

import com.wyett.server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/29 18:14
 * @description: http://127.0.0.1:8080/web-bus/queryUserInfo?userId=001
 */

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/queryUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public String queryUserInfo(HttpServletRequest request, HttpServletResponse response, String userId) {
        return userService.queryUserInfo(userId);
    }

}
