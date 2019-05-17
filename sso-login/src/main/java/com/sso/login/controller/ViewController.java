package com.sso.login.controller;

import com.sso.login.pojo.User;
import com.sso.login.utils.LoginCacheUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

/**
 * Created by Ryan on 2019/5/15/0015
 * 页面跳转逻辑
 */
@Controller
@RequestMapping("/view")
public class ViewController {

    @GetMapping("/login")
    public String toLogin(@RequestParam(required = false,defaultValue = "") String target,
                          HttpSession session,
                          @CookieValue(required = false,value = "TOKEN") Cookie cookie){
        if(StringUtils.isEmpty(target))
        {
            target ="http://www.codeshop.com:9010";
        }

        //如果是已经登录的用户再次访问登录系统时，就要重定向
        if(cookie!=null) {
            String value = cookie.getValue();
            User user = LoginCacheUtils.loginUser.get(value);
            if (user != null) {
                return "redirect:" + target;
            }
        }


        //重定向地址
        session.setAttribute("target",target);
        return "login";
    }
}
