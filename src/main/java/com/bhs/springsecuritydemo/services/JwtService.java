package com.bhs.springsecuritydemo.services;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Getter
    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    private SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<String,Object>(),userDetails);
    }

    public String generateToken(Map<String,Object> payload, UserDetails userDetails){
        return buildToken(payload,userDetails,expirationTime);
    }

    private String buildToken(Map<String,Object> payload, UserDetails userDetails, long expirationTime){
        return Jwts
                .builder()
                .subject(userDetails.getUsername())
                .signWith(getSignKey())
                .claims(payload)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (expirationTime * 1000L)))
                .compact();
    }

    public Claims extractAllClaims(String token){
        return (Claims) Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parse(token)
                .getPayload();
    }

    private <T>T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}
