package com.itcjx.dms.aspect;

import com.itcjx.dms.entity.OperationLog;
import com.itcjx.dms.mapper.OperationLogMapper;
import com.itcjx.dms.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Pointcut("@annotation(com.itcjx.dms.annotation.OperationLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // 执行方法
        Object result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        // 保存日志
        saveLog(point, time);
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        OperationLog operationLog = new OperationLog();
        operationLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()");
        operationLog.setTime(time);
        operationLog.setCreateTime(LocalDateTime.now());

        // 获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 获取IP地址
        operationLog.setIp(getIpAddress(request));

        // 获取操作用户
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String username = jwtTokenUtil.extractUsername(token);
                operationLog.setUsername(username);
            } catch (Exception e) {
                // 解析token失败
                operationLog.setUsername("unknown");
            }
        } else {
            operationLog.setUsername("unknown");
        }

        // 获取操作类型
        String operation = getOperation(joinPoint);
        operationLog.setOperation(operation);

        // 保存到数据库
        operationLogMapper.insert(operationLog);
    }

    private String getOperation(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        if (methodName.contains("create") || methodName.contains("add") || methodName.contains("save")) {
            return "创建";
        } else if (methodName.contains("update") || methodName.contains("edit")) {
            return "更新";
        } else if (methodName.contains("delete")) {
            return "删除";
        } else if (methodName.contains("get") || methodName.contains("find") || methodName.contains("query")) {
            return "查询";
        } else {
            return "操作";
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
