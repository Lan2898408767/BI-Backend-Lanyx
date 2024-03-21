package com.yupi.springbootinit.aop;

import com.yupi.springbootinit.annotation.CheckBanStatus;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class BanStatusInterceptor {

    @Resource
    private UserService userService; // 假设你有一个UserService来获取用户信息

    @Around("@annotation(checkBanStatus)")
    public Object checkBanStatus(ProceedingJoinPoint joinPoint, CheckBanStatus checkBanStatus) throws Throwable {
        // 获取当前请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 检查用户是否被封禁
        if (loginUser != null && UserConstant.BAN_ROLE.equals(loginUser.getUserRole())) {
            // 如果用户被封禁，抛出异常或返回特定的错误信息
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "你踏马号被我封了！！！");
        }

        // 用户未被封禁，继续执行原方法
        return joinPoint.proceed();
    }
}