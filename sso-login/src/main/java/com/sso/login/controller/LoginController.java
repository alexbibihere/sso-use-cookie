package com.sso.login.controller;

import com.sso.login.pojo.User;
import com.sso.login.utils.LoginCacheUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Ryan on 2019/5/15/0015
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static Set<User> dbUsers;
    static{
        dbUsers = new HashSet<>();
        User user = new User();
        user.setId("3");
        user.setUsername("zhangsan");
        user.setPassword("123");
        dbUsers.add(user);
//            dbUsers.add(new User("3","zhangsan","123123"));
//            dbUsers.add(new User(1,"lisi","123123"));
//            dbUsers.add(new User(0,"wangwu","1231"));
    }

    @PostMapping
    public String doLogin(User user, HttpSession session, HttpServletResponse response){

        String tartget = (String)  session.getAttribute("target");
    // 模拟从数据库中登录的用户名和密码
    Optional<User> first =  dbUsers.stream().filter(dbUser -> dbUser.getUsername().equals(user.getUsername()) && dbUser.getPassword() .equals(user.getPassword())).findFirst();
    //判断用户是否登录
    if(first.isPresent()){
        String token = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("TOKEN",token);
        cookie.setDomain("codeshop.com");
        response.addCookie(cookie);
        //保存用户登录信息
        LoginCacheUtils.loginUser.put(token,first.get());

    }else {
        session.setAttribute("msg","用户名或密码错误");
        return "login";
    }
    return "redirect:" + tartget;
    }

    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<User> getUserInfo(String token){
    if(!StringUtils.isEmpty(token)){
       User user = LoginCacheUtils.loginUser.get(token);
    return ResponseEntity.ok(user);
    }else {
        return new ResponseEntity<>((User) null,HttpStatus.BAD_REQUEST);
    }
    }


    @GetMapping("/logout")
    public String logout(HttpServletResponse response
    ,@CookieValue(required = true,value = "TOKEN") Cookie cookie
    ){
//        Cookie cookie = new Cookie("user","username");
       if(cookie!=null){
           cookie.setMaxAge(0);
           response.addCookie(cookie);
       }
        return "redirect:http://www.codeshop.com:9010/view/index";
    }
}
