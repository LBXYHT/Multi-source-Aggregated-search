package com.hutao.aggregatedsearch.aop;

import com.hutao.aggregatedsearch.annotation.AuthCheck;
import com.hutao.aggregatedsearch.common.ErrorCode;
import com.hutao.aggregatedsearch.exception.BusinessException;
import com.hutao.aggregatedsearch.model.entity.User;
import com.hutao.aggregatedsearch.model.enums.UserRoleEnum;
import com.hutao.aggregatedsearch.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 *
 */
// 标记为切面
@Aspect
// 组件
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    // 调用了 AuthCheck 注解的方法会进入这个切面
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取注解中定义的角色要求
        String mustRole = authCheck.mustRole();
        // 通过 RequestContextHolder 获取当前的 HTTP 请求 (与Spring Web 集成)
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 将角色字符串转化为枚举，获取角色属性
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 不需要权限，放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        // 必须有该权限才通过
        // 获取用户枚举属性
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        // 用户不存在
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 如果被封号，直接拒绝
        if (UserRoleEnum.BAN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 必须有管理员权限
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum)) {
            // 用户没有管理员权限，拒绝
            if (!UserRoleEnum.ADMIN.equals(userRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

