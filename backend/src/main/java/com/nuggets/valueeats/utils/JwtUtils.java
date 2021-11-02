package com.nuggets.valueeats.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public final class JwtUtils {
    @Value("${security.encryption.token_secret}")
    private String SECRET;

    
    /**
    * This utility method is used for creating a unique JSON web token.
    * 
    * @param    userId      An id that uniquely identifies a user
    * @return   A JSON web token.
    */
    public String encode(String userId) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        return Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(userId)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();
    }

    /**
    * This utility method is used for decoding a unique JSON web token.
    * 
    * @param    jwtToken      A JWT that uniquely identifies a user
    * @return   A user ID.
    */
    public String decode(String jwtToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(jwtToken)
                    .getBody()
                    .getSubject();
        } catch (JwtException ignored) {

        }

        return null;
    }
}
