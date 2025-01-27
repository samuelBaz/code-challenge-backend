//package com.example.backend.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.io.Serializable;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Component
//public class JwtUnauthorizedHandler implements AuthenticationEntryPoint, Serializable {
//
//    private static final long serialVersionUID = -2812636648800897280L;
//    private static final Logger logger = LoggerFactory.getLogger(JwtUnauthorizedHandler.class);
//
//    @Override
//    public void commence(HttpServletRequest request,
//                         HttpServletResponse response,
//                         AuthenticationException authException) throws IOException, ServletException {
//        // Log para monitoreo
//        logger.warn("Unauthorized access attempt: {}", request.getRequestURI());
//        logger.warn("Error details: {}", authException.getMessage());
//
//        // Manejo de CORS (opcional)
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
//
//        // Respuesta personalizada
//        String message = authException.getMessage().contains("expired") ?
//                "Token expired" : "Unauthorized access";
//
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
//        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
//    }
//}
