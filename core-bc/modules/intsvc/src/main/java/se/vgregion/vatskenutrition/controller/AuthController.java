package se.vgregion.vatskenutrition.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.vgregion.vatskenutrition.model.ApplicationUser;
import se.vgregion.vatskenutrition.model.Article;
import se.vgregion.vatskenutrition.model.jwt.JwtToken;
import se.vgregion.vatskenutrition.model.jwt.RefreshToken;
import se.vgregion.vatskenutrition.service.ArticleService;
import se.vgregion.vatskenutrition.service.JwtTokenFactory;
import se.vgregion.vatskenutrition.spring.WebSecurity;
import se.vgregion.vatskenutrition.spring.WebSecurityConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static se.vgregion.vatskenutrition.security.JWTAuthorizationFilter.TOKEN_PREFIX;

@Controller
@RequestMapping("/auth")
public class AuthController {

    public static String HEADER_PREFIX = "Bearer "; // todo Make constant somewhere
    private final String secret = "SECRET"; // todo Make property

    @Autowired
    private JwtTokenFactory tokenFactory;

    @RequestMapping(value="/token", method=RequestMethod.GET, produces={ MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<JwtToken> refreshToken(HttpServletRequest request, HttpServletResponse response) {

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

        return ResponseEntity.ok(tokenFactory.createAccessJwtToken(applicationUser));
    }
}
