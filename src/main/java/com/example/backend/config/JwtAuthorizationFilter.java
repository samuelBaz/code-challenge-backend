//package com.example.backend.config;
//
//import com.example.backend.service.TokenService;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.JwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class JwtAuthorizationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private TokenService tokenService;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//        String token = tokenService.extractToken(req);
//        if (token != null && !token.trim().isEmpty()) {
//            try {
//                if (!tokenService.isTokenValid(token)) {
//                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    res.setContentType("application/json");
//                    res.getWriter().write("{\"error\": \"Invalid or expired token\"}");
//                    SecurityContextHolder.clearContext();
//                    return;
//                }
//
//                String principal = tokenService.getTokenInformationAsString(token);
//                JsonNode jsonNode = objectMapper.readTree(principal);
//
//                String username = jsonNode.get("username").asText(); // Extract specific data
//                JsonNode json = jsonNode.get("authorities");
//
//                if (json != null && json.isArray()) {
//                    List<GrantedAuthority> authorities = new ArrayList<>();
//                    json.forEach(authority -> {
//                        if (authority.has("authority")) {
//                            authorities.add(new SimpleGrantedAuthority(authority.get("authority").textValue()));
//                        }
//                    });
//
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(username, null, authorities);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            } catch (JwtException e) {
//                logger.error("Error validating the token", e);
//                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                res.setContentType("application/json");
//                res.getWriter().write("{\"error\": \"Invalid token\"}");
//                SecurityContextHolder.clearContext();
//                return;
//            }
//        }
//        chain.doFilter(req, res);
//    }
//
//}
//
