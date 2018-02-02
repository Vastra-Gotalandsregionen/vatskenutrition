package se.vgregion.vatskenutrition.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.vgregion.vatskenutrition.model.ApplicationUser;
import se.vgregion.vatskenutrition.model.jwt.JwtToken;
import se.vgregion.vatskenutrition.service.JwtTokenFactory;
import se.vgregion.vatskenutrition.spring.WebSecurity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static se.vgregion.vatskenutrition.security.JWTAuthorizationFilter.TOKEN_PREFIX;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public static String HEADER_PREFIX = "Bearer "; // todo Make constant somewhere
    private final String secret = "SECRET"; // todo Make property

    @Autowired
    private JwtTokenFactory tokenFactory;

    @RequestMapping(value="/token", produces={ MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String header = request.getHeader(WebSecurity.AUTHENTICATION_HEADER_NAME);

        String tokenPayload = header.substring(HEADER_PREFIX.length(), header.length());

        Claims body = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(tokenPayload.replace(TOKEN_PREFIX, ""))
                .getBody();

        String jti = body.getId();

        List scopes = body.get("scopes", List.class);

        if (!scopes.contains("ROLE_REFRESH_TOKEN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String subject = body.getSubject();

        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUsername(subject);
        applicationUser.setAuthorities(Collections.emptyList());

        JwtToken accessToken = tokenFactory.createAccessJwtToken(applicationUser);
        JwtToken refreshToken = tokenFactory.createRefreshToken(applicationUser);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken.getToken());
        tokenMap.put("refreshToken", refreshToken.getToken());

        return ResponseEntity.ok(tokenMap);
    }
}
