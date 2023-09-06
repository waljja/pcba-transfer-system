package com.ht.util;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ht.entity.UserEntity;

/**
 * 保存和获取当前用户的工具类
 * Created by lenovo on 2020/1/16.
 */
public class UserContext {
    private static final String CURRENT_USER_IN_SESSION = "User";
    /**
     * 得到session
     */
    private static HttpSession getSession(){
        //SpringMVC获取session的方式通过RequestContextHolder
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
    }
    /**
     * 设置当前用户到session中
     */
    public static void putCurrebtUser(String currentUser) {
       getSession().setAttribute(CURRENT_USER_IN_SESSION,currentUser);
    }
    /**
     * 获取当前用户
     */
    public static String getCurreentUser() {
        return (String) getSession().getAttribute(CURRENT_USER_IN_SESSION);
    }
}
