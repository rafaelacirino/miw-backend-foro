package es.upm.miw.foro.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.service.impl.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private static final Long ID = 1L;
    private static final String FIRST_NAME = "UserName";
    private static final String LAST_NAME = "UserLastName";
    private static final String EMAIL = "email@email.com";
    private static final String ROLE = "GUEST";

    @Test
    void testUserWithInvalidHeaderThrowsException() {
        assertThrows(JWTDecodeException.class, () -> jwtService.user("Not Bearer"));
    }

    @Test
    void testCreateTokenAndVerify() {
        String token = jwtService.createToken(ID, FIRST_NAME, LAST_NAME, EMAIL, ROLE);
        assertEquals(3, token.split("\\.").length);
        assertTrue(token.length() > 30);
        assertEquals(EMAIL, jwtService.user(token));
        assertEquals(EMAIL, jwtService.name(token));
        assertEquals(ROLE, jwtService.role(token));
    }

    @Test
    void testExtractTokenWithNoBearer() {
        assertNull(jwtService.extractToken("t.t.t")); // Sem "Bearer "
    }

    @Test
    void testExtractTokenWithInvalidFormat() {
        assertNull(jwtService.extractToken("Bearer invalid"));
    }

    @Test
    void testUserWithInvalidTokenThrowsException() {
        assertThrows(JWTDecodeException.class, () -> jwtService.user("Bearer invalid"));
    }

    @Test
    void testRoleWithInvalidTokenThrowsException() {
        assertThrows(JWTDecodeException.class, () -> jwtService.role("invalid"));
    }

    @Test
    void testVerifyWithValidToken() {
        String token = jwtService.createToken(ID, FIRST_NAME, LAST_NAME, EMAIL, ROLE);
        Optional<DecodedJWT> decodedJWT = jwtService.verify(token);
        assertTrue(decodedJWT.isPresent());
        assertEquals(EMAIL, decodedJWT.get().getClaim("email").asString());
        assertEquals(ROLE, decodedJWT.get().getClaim("role").asString());
    }

    @Test
    void testVerifyWithInvalidToken() {
        Optional<DecodedJWT> decodedJWT = jwtService.verify("invalid.token.here");
        assertFalse(decodedJWT.isPresent());
    }

    @Test
    void testNameWithInvalidToken() {
        assertEquals("", jwtService.name("Bearer invalid"));
    }

    @Test
    void testUserWithNoEmailClaimThrowsException() {
        String token = JWT.create()
                .withIssuer("test-issuer")
                .withClaim("role", ROLE)
                .sign(Algorithm.HMAC256("test-secret"));
        assertThrows(JWTDecodeException.class, () -> jwtService.user(token));
    }
}