package es.upm.miw.foro.service.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.environment:dev}")
    public String environment;

    public boolean sendPasswordResetEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            String token = jwtService.createPasswordResetToken(email);
            String resetLink = String.format("%s/reset-password?token=%s",
                    environment.equals("dev") ? "http://localhost:4200" : "https://capturing-forum.onrender.com",
                    token);
            try {
                emailService.sendPasswordResetEmail(email, resetLink);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void  resetPassword(String token, String newPassword) {
        try {
            String email = jwtService.validatePasswordResetToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ServiceException("User not found"));

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (JWTVerificationException e) {
            throw new ServiceException("Invalid or expired token");
        }
    }
}

