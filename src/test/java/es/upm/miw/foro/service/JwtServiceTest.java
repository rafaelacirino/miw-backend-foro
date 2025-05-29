package es.upm.miw.foro.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.service.impl.JwtServiceImpl;
import es.upm.miw.foro.util.MessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class JwtServiceTest {

    @Autowired
    private JwtServiceImpl jwtServiceImpl;

    private static final Long ID = 1L;
    private static final String FIRST_NAME = "UserName";
    private static final String LAST_NAME = "UserLastName";
    private static final String EMAIL = "email@email.com";
    private static final String ROLE = "GUEST";

    @Test
    void testUserWithInvalidHeaderThrowsException() {
        assertThrows(JWTDecodeException.class, () -> jwtServiceImpl.user("Not Bearer"));
    }

    @Test
    void testCreateTokenAndVerify() {
        String token = jwtServiceImpl.createToken(ID, FIRST_NAME, LAST_NAME, EMAIL, ROLE);
        assertEquals(3, token.split("\\.").length);
        assertTrue(token.length() > 30);
        assertEquals(EMAIL, jwtServiceImpl.user(token));
        assertEquals(ROLE, jwtServiceImpl.role(token));
    }

    @Test
    void testExtractTokenWithValidToken() {
        String validToken = "Bearer header.payload.signature";
        assertEquals("header.payload.signature", jwtServiceImpl.extractToken(validToken));
    }

    @Test
    void testExtractTokenWithNullHeader() {
        assertNull(jwtServiceImpl.extractToken(null));
    }

    @Test
    void testExtractTokenWithNoBearer() {
        assertNull(jwtServiceImpl.extractToken("t.t.t")); // Sem "Bearer "
    }

    @Test
    void testExtractTokenWithInvalidFormat() {
        assertNull(jwtServiceImpl.extractToken("Bearer invalid"));
    }

    @Test
    void testUserWithInvalidTokenThrowsException() {
        assertThrows(JWTDecodeException.class, () -> jwtServiceImpl.user("Bearer invalid"));
    }

    @Test
    void testExtractWithEmptyToken() {
        assertNull(jwtServiceImpl.extractToken("Bearer "));
    }

    @Test
    void testExtractWithTwoParts() {
        assertNull(jwtServiceImpl.extractToken("Bearer part1.part2"));
    }

    @Test
    void testExtractWithFourParts() {
        assertNull(jwtServiceImpl.extractToken("Bearer part1.part2.part3.part4"));
    }

    @Test
    void testRoleWithInvalidTokenThrowsException() {
        assertThrows(JWTDecodeException.class, () -> jwtServiceImpl.role("invalid"));
    }

    @Test
    void testCreatePasswordResetToken() {
        String token = jwtServiceImpl.createPasswordResetToken(EMAIL);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals(EMAIL, decodedJWT.getSubject());
        assertTrue(decodedJWT.getClaim(MessageUtil.PASSWORD_RESET_CLAIM).asBoolean());
        assertNull(jwtServiceImpl.role(token));
    }

    @Test
    void testValidatePasswordResetTokenReturnsEmail() {
        String token = jwtServiceImpl.createPasswordResetToken(EMAIL);

        String resultEmail = jwtServiceImpl.validatePasswordResetToken(token);
        assertEquals(EMAIL, resultEmail);
    }

    @Test
    void testValidatePasswordResetTokenThrowsOnInvalidClaim() {
        String token = JWT.create()
                .withIssuer("test-issuer")
                .withSubject(EMAIL)
                .sign(Algorithm.HMAC256("test-secret"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jwtServiceImpl.validatePasswordResetToken(token));
        assertEquals("Invalid or expired password reset token", exception.getMessage());
    }

    @Test
    void testValidatePasswordResetTokenThrowsOnTamperedToken() {
        String token = jwtServiceImpl.createPasswordResetToken(EMAIL);
        String tamperedToken = token.substring(0, token.length() - 1) + "x";

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jwtServiceImpl.validatePasswordResetToken(tamperedToken));
        assertEquals("Invalid or expired password reset token", exception.getMessage());
    }

    @Test
    void testVerifyWithValidToken() {
        String token = jwtServiceImpl.createToken(ID, FIRST_NAME, LAST_NAME, EMAIL, ROLE);
        Optional<DecodedJWT> decodedJWT = jwtServiceImpl.verify(token);
        assertTrue(decodedJWT.isPresent());
        assertEquals(EMAIL, decodedJWT.get().getClaim("email").asString());
        assertEquals(ROLE, decodedJWT.get().getClaim("role").asString());
    }

    @Test
    void testVerifyWithInvalidToken() {
        Optional<DecodedJWT> decodedJWT = jwtServiceImpl.verify("invalid.token.here");
        assertFalse(decodedJWT.isPresent());
    }

    @Test
    void testUserWithNoEmailClaimThrowsException() {
        String token = JWT.create()
                .withIssuer("test-issuer")
                .withClaim("role", ROLE)
                .sign(Algorithm.HMAC256("test-secret"));
        assertThrows(JWTDecodeException.class, () -> jwtServiceImpl.user(token));
    }

    @Test
    void testUserWithEmptyEmailClaimThrowsException() {
        String token = JWT.create()
                .withIssuer("test-issuer")
                .withClaim("email", "")
                .withClaim("role", ROLE)
                .sign(Algorithm.HMAC256("test-secret"));

        JWTDecodeException exception = assertThrows(JWTDecodeException.class, () -> jwtServiceImpl.user("Bearer " + token));
        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void testRoleWithNullTokenThrowsException() {
        JWTDecodeException exception = assertThrows(JWTDecodeException.class, () -> jwtServiceImpl.role(null));
        assertEquals("Invalid token format", exception.getMessage());
    }

    @Test
    void testRoleWithInvalidTokenFormatThrowsException() {
        JWTDecodeException exception = assertThrows(JWTDecodeException.class, () -> jwtServiceImpl.role("invalid.token"));
        assertEquals("Invalid token format", exception.getMessage());
    }

    @Test
    void testRoleWithDecodingFailureThrowsException() {
        String malformedToken = "invalid.token.format";

        JWTDecodeException exception = assertThrows(JWTDecodeException.class, () -> jwtServiceImpl.role(malformedToken));
        assertNotNull(exception.getMessage());
    }
}