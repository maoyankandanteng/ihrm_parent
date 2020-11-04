package com.ihrm.common.utils;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Setter
@Getter
@ConfigurationProperties(prefix = "jwt.config")
@EnableConfigurationProperties(JwtUtils.class)
public class JwtUtils {
    //签名密钥
    private String key;

    //签名的失效时间
    private Long ttl;

    //设置认证token
    // id: 登陆用户id
    // subject 登陆用户名
    public String createJwt(Map<String,Object> map){
        long now= System.currentTimeMillis();
        long exp=now+ttl;
        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(exp))
                .signWith(SignatureAlgorithm.HS256, key);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jwtBuilder.claim(entry.getKey(),entry.getValue());
        }
       return jwtBuilder.compact();
    }

    public Claims parseJwt(String token){
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }
}
