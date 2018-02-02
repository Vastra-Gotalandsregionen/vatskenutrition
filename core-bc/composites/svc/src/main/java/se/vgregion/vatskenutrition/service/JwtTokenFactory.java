package se.vgregion.vatskenutrition.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import se.vgregion.vatskenutrition.model.ApplicationUser;
import se.vgregion.vatskenutrition.model.jwt.AccessJwtToken;
import se.vgregion.vatskenutrition.model.jwt.JwtToken;
import se.vgregion.vatskenutrition.model.jwt.JwtUser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtTokenFactory {

    private static final String ISSUER = "Vatskenutrition"; // todo Make property
    private static final int EXPIRATION_MINUTES = 60; // todo Make property
    private static final int REFRESH_TOKEN_EXPIRATION_TIME = 120; // todo Make property

    private final String signingKey = "SECRET";

    @Autowired
    public JwtTokenFactory() {
    }

    public AccessJwtToken createAccessJwtToken(ApplicationUser applicationUser) {
        if (StringUtils.isEmpty(applicationUser.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username");

//        if (applicationUser.getAuthorities() == null || applicationUser.getAuthorities().isEmpty())
//            throw new IllegalArgumentException("User doesn't have any privileges");

        Claims claims = Jwts.claims().setSubject(applicationUser.getUsername());
        claims.put("scopes", applicationUser.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
        JwtUser jwtUser = new JwtUser();
        jwtUser.setDisplayName(applicationUser.getDisplayName());
        claims.put("context", jwtUser);

        Date now = new Date();

        String token = Jwts.builder()
          .setClaims(claims)
          .setIssuer(ISSUER)
          .setIssuedAt(now)
          .setExpiration(Date.from(Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES)))
          .signWith(SignatureAlgorithm.HS512, signingKey)
        .compact();

        return new AccessJwtToken(token, claims);
    }

    public JwtToken createRefreshToken(ApplicationUser applicationUser) {
        if (StringUtils.isEmpty(applicationUser.getUsername())) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }

        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().setSubject(applicationUser.getUsername());
        claims.put("scopes", Arrays.asList("ROLE_REFRESH_TOKEN"));
        JwtUser jwtUser = new JwtUser();
        jwtUser.setDisplayName(applicationUser.getDisplayName());
        claims.put("context", jwtUser);

        String token = Jwts.builder()
          .setClaims(claims)
          .setIssuer(ISSUER)
          .setId(UUID.randomUUID().toString())
          .setIssuedAt(Date.from(currentTime.atOffset(ZoneOffset.UTC).toInstant()))
          .setExpiration(Date.from(currentTime.plusMinutes(REFRESH_TOKEN_EXPIRATION_TIME).atOffset(ZoneOffset.UTC).toInstant()))
          .signWith(SignatureAlgorithm.HS512, signingKey)
        .compact();

        return new AccessJwtToken(token, claims);
    }
}