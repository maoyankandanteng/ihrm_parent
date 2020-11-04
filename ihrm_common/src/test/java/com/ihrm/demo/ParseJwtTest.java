package com.ihrm.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class ParseJwtTest {
    public static void main(String[] args) {
        String token="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJpYXQiOjE2MDExNzcyODEsImNvbXBhbnlJZCI6IjEyMzQ1NiIsImNvbXBhbnlOYW1lIjoi5YyX5Lqs5rWp5Z2k56eR5oqA5pyJ6ZmQ5YWs5Y-4In0.zfg2oBIgoLrIuPk3fdJ-7uZEus342dOFJj5qtSao0ow";
        Claims claim = Jwts.parser().setSigningKey("ihrm").parseClaimsJws(token).getBody();
        System.out.println(claim.getId());
        System.out.println(claim.getSubject());
        System.out.println(claim.getIssuedAt());

        //解析自定义claim内容
        System.out.println(claim.get("companyId"));
        System.out.println(claim.get("companyName"));
    }
}
