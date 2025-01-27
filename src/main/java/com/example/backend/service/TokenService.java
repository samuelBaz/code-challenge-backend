package com.example.backend.service;

import com.example.backend.util.DateGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TokenService {
    // Spring Security
    public static final String HEADER_AUTHORIZACION_KEY = "Authorization";
    public static final String UNAUTHORIZED_USER_MESSAGE = "User not authorized to perform the action, please sign in" +
            " again";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";
    public static final String TOKEN = "token";
    public static final String UNDEFINED = "undefined";


    @Value("${spring.secret.tokenKey}")
    private String secretKey;
    private ObjectMapper objectMapper;

    public TokenService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * This method generates a token compressing the information send by
     * parameter
     *
     * @param numberOfDays    Amount of time to keep the token active
     * @param objectToConvert
     * @return String Token generated in format JWT
     * @throws JsonProcessingException
     */
    public <T> String generateTokenByDay(int numberOfDays, T objectToConvert, boolean isBearerRequired)
            throws JsonProcessingException {
        String convertedObject = (objectToConvert instanceof String) ? (String) objectToConvert
                : objectMapper.writeValueAsString(objectToConvert);
        String compactJws = Jwts.builder().setSubject(convertedObject)
                .setExpiration(DateGenerator.addDaysToDate(numberOfDays)).signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return isBearerRequired ? TOKEN_BEARER_PREFIX + compactJws : compactJws;
    }

    public void validateToken(String token) {
        Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token.replace(TOKEN_BEARER_PREFIX, ""));
    }

    public <T> T getTokenInformation(String token, Class<T> classType) throws IOException {
        String jsonObject = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token.replace(TOKEN_BEARER_PREFIX, "")).getBody().getSubject();
        return objectMapper.readValue(jsonObject, classType);
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZACION_KEY);
        if (header != null && header.startsWith(TOKEN_BEARER_PREFIX)) {
            return header;
        }
        String token = request.getParameter(TOKEN);
        if (token != null && !token.equalsIgnoreCase(UNDEFINED)) {
            return token;
        }
        return null;
    }

    public String getTokenInformationAsString(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token.replace(TOKEN_BEARER_PREFIX, "")).getBody().getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // Llave para validar la firma
                    .build()
                    .parseClaimsJws(token);   // Valida firma, estructura, etc.
            return true;
        } catch (JwtException e) {
            return false; // Token inválido
        }
    }

}
