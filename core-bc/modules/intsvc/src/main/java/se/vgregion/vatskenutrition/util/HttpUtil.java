package se.vgregion.vatskenutrition.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static se.vgregion.vatskenutrition.util.Constants.HEADER_STRING;
import static se.vgregion.vatskenutrition.util.Constants.TOKEN_PREFIX;

/**
 * @author Patrik Björk
 */
@Component
public class HttpUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getUserIdFromRequest(HttpServletRequest request) {
        String userId = null;

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = request.getHeader(HEADER_STRING);
            if (token != null) {
                // parse the token.
                userId = Jwts.parser()
                        .setSigningKey(jwtSecret)
                        .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                        .getBody()
                        .getSubject();
            }
        }

        return userId;
    }
}
