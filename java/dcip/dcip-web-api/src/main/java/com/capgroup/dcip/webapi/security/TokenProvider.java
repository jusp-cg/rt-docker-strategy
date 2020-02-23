package com.capgroup.dcip.webapi.security;

import com.capgroup.dcip.webapi.security.okta.ClaimsAuthoritiesExtractor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class TokenProvider {
    private static final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    @Autowired
    private SigningKeyResolver signingKeyResolver;

    @Value("${oidc.authoritiesKey:scp}") // where to parse scopes from the JWT, which become entitlements/roles
    private String authoritiesKey;

    @Value("${oidc.audience}")
    private String audience;

    public Authentication getAuthentication(String token) {
        System.err.println("TokenProvider:" + token);

        Claims claims = Jwts.parser().setSigningKeyResolver(signingKeyResolver).parseClaimsJws(token).getBody();
        log.trace("Obtained claims from token");
        // we tell Spring Security which entitlements this user has based on what's in
        // the "scp" in the token.
        ClaimsAuthoritiesExtractor claimsExtractor = new ClaimsAuthoritiesExtractor(authoritiesKey);
        Collection<? extends GrantedAuthority> authorities = claimsExtractor.extractAuthorities(claims);
        log.trace("Transformed claims from {} parameter in JWT to Spring Security authorities", authoritiesKey);
        User principal = new User(claims.getSubject(), "", authorities);
        log.trace("Created User principal");
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        System.err.println("TokenProvider, validate:" + authToken);
        try {
            Jwts.parser().setSigningKeyResolver(signingKeyResolver).requireAudience(audience).parseClaimsJws(authToken);
            return true;
            //} catch (SignatureException e) {
            //	log.info("Invalid JWT signature.");
            //	log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("Bad JWT token due to IllegalArgumentException");
            log.trace("JJWT threw IllegalArgumentException! trace: {}", e);
        }
        return false;
    }
}