package com.business.user_service.jwt;

import com.business.user_service.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private final String SECRET_KEY = "your_secret_key"; // Thay thế bằng khóa bí mật của bạn
    private final long EXPIRATION_TIME = 86400000; // 1 ngày

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getPhone())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token, User user) {
        final String phone = extractPhone(token);
        return (phone.equals(user.getPhone()) && !isTokenExpired(token));
    }

    public String extractPhone(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
