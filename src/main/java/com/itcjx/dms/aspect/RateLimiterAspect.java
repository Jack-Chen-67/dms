package com.itcjx.dms.aspect;

import com.google.common.util.concurrent.RateLimiter; // Google Guava 的 RateLimiter
import com.itcjx.dms.annotation.RateLimiterAnno; // 项目内部的 RateLimiter 注解
import com.itcjx.dms.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimiterAspect {

    /**
     * 用来存放不同接口的RateLimiter(key为接口名称，value为RateLimiter)
     */
    private static final ConcurrentHashMap<String, RateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();

    @Pointcut("@annotation(com.itcjx.dms.annotation.RateLimiterAnno)")
    public void rateLimit() {
    }

    @Around("rateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimiter rateLimiter = null; // 使用 Google Guava 的 RateLimiter 类

        // 通过注解获取rateLimiter
        RateLimiterAnno rateLimiterAnnotation = method.getAnnotation(RateLimiterAnno.class);
        if (rateLimiterAnnotation != null) {
            String key = rateLimiterAnnotation.key();
            if (key == null || key.isEmpty()) {
                key = method.getName();
            }

            // 获取请求IP作为限流key的一部分
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = getClientIpAddress(request);
                key = key + ":" + ip;
            }

            double count = rateLimiterAnnotation.count();
            int time = rateLimiterAnnotation.time();

            // 计算每秒放置令牌的个数
            double permitsPerSecond = count / time;

            if (RATE_LIMITER_CACHE.containsKey(key)) {
                rateLimiter = RATE_LIMITER_CACHE.get(key);
            } else {
                rateLimiter = RateLimiter.create(permitsPerSecond);
                RATE_LIMITER_CACHE.put(key, rateLimiter);
            }

            // 获取令牌，如果获取不到，等待一小段时间
            boolean tryAcquire = rateLimiter.tryAcquire(1, 100, TimeUnit.MILLISECONDS);
            if (!tryAcquire) {
                return Result.error(429, rateLimiterAnnotation.limitTip());
            }
        }

        return point.proceed();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
