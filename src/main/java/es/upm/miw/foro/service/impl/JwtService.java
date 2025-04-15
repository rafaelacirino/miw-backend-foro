package es.upm.miw.foro.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class JwtService {

    private static final String FIRSTNAME_CLAIM = "firstName";
    private static final String LASTNAME_CLAIM = "lastName";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";

    private static final String PASSWORD_RESET_CLAIM = "pwd_reset";

    private final String secret;
    private final String issuer;
    private final int expire;

    @Autowired
    public JwtService(@Value("${miw.jwt.secret}") String secret, @Value("${miw.jwt.issuer}") String issuer,
                      @Value("${miw.jwt.expire}") int expire) {
        this.secret = secret;
        this.issuer = issuer;
        this.expire = expire;
    }

    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (token.split("\\.").length == 3) {
                return token;
            }
        }
        return null;
    }

    public String createToken(Long id, String firstName, String lastName, String email, String role) {
        return JWT.create()
                .withIssuer(this.issuer)
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + this.expire * 1000L))
                .withClaim("id", id)
                .withClaim(FIRSTNAME_CLAIM, firstName)
                .withClaim(LASTNAME_CLAIM, lastName)
                .withClaim(EMAIL_CLAIM, email)
                .withClaim(ROLE_CLAIM, role)
                .sign(Algorithm.HMAC256(this.secret));
    }

    public String user(String authorization) {
        return this.verify(authorization)
                .map(jwt -> {
                    String email = jwt.getClaim(EMAIL_CLAIM).asString();
                    if (email == null || email.isEmpty()) {
                        log.error("Email claim not found in token");
                        throw new JWTDecodeException("Email claim not found in token");
                    }
                    return email;
                })
                .orElseThrow(() -> new JWTDecodeException("Invalid token"));
    }

    public String role(String token) {
        log.info("Token to decode: {}", token);
        if (token == null || token.split("\\.").length != 3) {
            log.error("Invalid token format");
            throw new JWTDecodeException("Invalid token format");
        }
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String role = decodedJWT.getClaim("role").asString();
            log.info("Extracted role from token: {}", role);
            return role;
        } catch (JWTDecodeException e) {
            log.error("Failed to decode token: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<DecodedJWT> verify(String token) {
        try {
            return Optional.of(JWT.require(Algorithm.HMAC256(this.secret))
                    .withIssuer(this.issuer).build()
                    .verify(token));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public String createPasswordResetToken(String email) {
        return JWT.create()
                .withIssuer(this.issuer)
                .withSubject(email)
                .withClaim(PASSWORD_RESET_CLAIM, true)
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000L))
                .sign(Algorithm.HMAC256(this.secret));
    }

    public String validatePasswordResetToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(this.secret))
                    .withIssuer(this.issuer)
                    .withClaim(PASSWORD_RESET_CLAIM, true)
                    .build()
                    .verify(token);

            return jwt.getSubject();
        } catch (Exception e) {
            log.error("Invalid password reset token: {}", e.getMessage());
            throw new RuntimeException("Invalid or expired password reset token");
        }
    }
}
