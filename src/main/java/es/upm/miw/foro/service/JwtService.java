package es.upm.miw.foro.service;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Optional;

public interface JwtService {

    String extractToken(String authHeader);

    String createToken(Long id, String firstName, String lastName, String email, String role);

    Optional<DecodedJWT> verify(String token);

    String createPasswordResetToken(String email);

    String validatePasswordResetToken(String token);
}
