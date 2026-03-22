package com.taskmanager.taskservice.infrastructure.filter;

import com.taskmanager.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret:ZGV2LW9ubHktcGxhY2Vob2xkZXIta2V5LWNoYW5nZS1tZS1pbi1wcm9kdWN0aW9uIQ==}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                Long userId = claims.get("userId", Long.class);
                String role = claims.get("role", String.class);
                String username = claims.getSubject();

                request.setAttribute("userId", userId);
                request.setAttribute("role", role);
                request.setAttribute("username", username);
            } catch (Exception e) {
                throw new UnauthorizedException("Invalid or expired token");
            }
        }

        filterChain.doFilter(request, response);
    }
}

