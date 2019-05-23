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


    /**
     * 删除Cookie时，只设置maxAge=0将不能够从浏览器中删除cookie,
     * 因为一个Cookie应当属于一个path与domain，所以删除时，Cookie的这两个属性也必须设置。
     *
     * 误区:刚开始时，我没有发现客户端发送到服务器端的cookie的path与domain值为空这个问题。
     * 因为在登陆系统时，我设置了Cookie的path与domain属性的值,就误认为每次客户端请求时，都会把Cookie的
     * 这两个属性也提交到服务器端，但系统并没有把path与domain提交到服务器端(提交过来的只有Cookie的key，value值)。
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response
    ){

        Cookie[] cookies = request.getCookies();
        for(Cookie cookie:cookies){
        if(cookie!=null && "TOKEN".equals(cookie.getName())){
            //在删除cookie时，只设置maxAge=0不能够从浏览器中删除cookie
            //因为一个Cookie应该属于一个path和domain，所以删除时，Cookie的这两个值也必须设置。

           cookie.setMaxAge(0);
           cookie.setDomain("codeshop.com");
           cookie.setPath("/"); //设置为‘/’ 都可以访问
           response.addCookie(cookie);
       }}
        return "redirect:http://login.codeshop.com:9000/view/login";
    }
}
