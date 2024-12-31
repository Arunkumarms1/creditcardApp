package com.bank.creditcard.utils;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {
    private static final String SECRET = "sr57ndjab2406dc08170b798af5e7e1b861d4d3774680bbaba89d74514e66b7db30a4ddaa66f21237b9a0a7e79b255c48ca3c6ddd367075a7a7d6dc6807aebaa2a38c1819e8cdd780eb8664e6ecfb551ac37a3be94dbb6ef482f02896c57c98069c29612620dc05da0b93fe55489c266f6f819606dc88cc2d2074d005c14f26ab31ad3146d9419aaa319af21674e91ab716da137f02203fa1bb045c06dff7f5052af10a64246edd3b79ec4853e5ebc446681957c39b7e6b230af151eb7a6ec25c9b5f506e4f1ad5c747806638d88426b6c04a5242d5a995bf059a9dbcf335de22ca9f14a0b0690f0934e1a43e0f93de40e71d84677e18ee5e6f2e6a2977d2014";

    private static final Logger log = LogManager.getLogger(JwtService.class);

    public String generateToken(BankUser bankUser) {
        List<String> roleNames = bankUser.getRoles().stream()
                .map(Role::getAuthority)
                .toList();
        Claims claims = Jwts.claims().add("roles", roleNames).build();
        return Jwts.builder().claims(claims)
                .subject(bankUser.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSignKey()).compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return (List<String>) claims.get("roles");
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean isTokenExpired(String token) {
        return !Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().getExpiration()
                .before(new Date());
    }

    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && isTokenExpired(token);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
