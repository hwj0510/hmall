package com.hmall.common.interceptors;

import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //1.获取登录用户信息
        String userInfo = request.getHeader("user-info");
        System.out.println("userInfo = " + userInfo);
        if (StrUtil.isNotBlank(userInfo)){
            UserContext.setUser(Long.valueOf(userInfo));
        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.removeUser();
    }
}
