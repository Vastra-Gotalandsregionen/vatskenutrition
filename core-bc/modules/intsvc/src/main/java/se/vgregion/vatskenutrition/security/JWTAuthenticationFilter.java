package se.vgregion.vatskenutrition.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import se.vgregion.vatskenutrition.model.ApplicationUser;
import se.vgregion.vatskenutrition.model.jwt.JwtToken;
import se.vgregion.vatskenutrition.service.JwtTokenFactory;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    private JwtTokenFactory jwtTokenFactory;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenFactory jwtTokenFactory) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenFactory = jwtTokenFactory;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            ApplicationUser creds = new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {
        UserDetails principal = (UserDetails) auth.getPrincipal();

        ApplicationUser user = new ApplicationUser();
        user.setUsername(principal.getUsername());
        user.setAuthorities(new ArrayList<>());

        if (principal instanceof InetOrgPerson) {
            InetOrgPerson ldapUserDetails = (InetOrgPerson) principal;
            user.setDisplayName(ldapUserDetails.getDisplayName());
        } else {
            user.setDisplayName(principal.getUsername());
        }

        JwtToken accessToken = jwtTokenFactory.createAccessJwtToken(user);
        JwtToken refreshToken = jwtTokenFactory.createRefreshToken(user);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken.getToken());
        tokenMap.put("refreshToken", refreshToken.getToken());

        res.setStatus(HttpStatus.OK.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(res.getWriter(), tokenMap);
    }
}