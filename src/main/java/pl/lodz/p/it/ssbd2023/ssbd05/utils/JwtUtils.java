package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtils {

    @Inject
    private Properties properties;

    private Key getSigningKey() {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJWT(Account account) {

        return Jwts.builder()
            .setSubject(account.getLogin())
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
}