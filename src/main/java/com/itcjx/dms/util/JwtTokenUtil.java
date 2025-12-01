package com.itcjx.dms.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenUtil {

    // 密钥
    @Value("${jwt.secret}")
    private String secret;
    // 过期时间， 24 小时
    private long expiration = 86400000; // 毫秒

    // 从token中获取用户名
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    // 生成token
    public String generateToken(String username, Long userId) {
        return Jwts.builder()
                .claim("UserId", userId)
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, Base64.getDecoder().decode(secret))
                .compact();
    }

    // 验证token是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(Base64.getDecoder().decode(secret)).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //解析JWT令牌
    public String parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(secret))
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    //从令牌中获取用户ID
    public Long getUserIdFromToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token 为空");
        }
        token = token.trim();
        return Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(secret))
                .parseClaimsJws(token)
                .getBody()
                .get("UserId", Long.class);
    }




    // 获取过期时间
    public Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // 检查是否过期
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
