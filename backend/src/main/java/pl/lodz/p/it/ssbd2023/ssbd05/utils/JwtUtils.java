package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtils {

    @Inject
    private Properties properties;

    private Key getSigningKey() {
        byte[] keyBytes = properties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJWT(Account account) {

        return Jwts.builder()
            .setSubject(account.getLogin())
            .setIssuer(properties.getIssuer())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + properties.getJwtExpirationTime()))
            .claim("groups", account.getAccessLevels()
                .stream()
                .filter(AccessLevel::isActive)
                .map(AccessLevel::getLevel)
                .toList())
            .signWith(this.getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    public Jws<Claims> parseJWT(String jwt) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey()).build()
            .parseClaimsJws(jwt);
    }


    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }

    public boolean validateToken(String jwt) {
        try {
            parseJWT(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
