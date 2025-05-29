package es.upm.miw.foro.service.email;

import com.auth0.jwt.exceptions.JWTVerificationException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.UserRepository;
import es.upm.miw.foro.service.impl.JwtServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final JwtServiceImpl jwtServiceImpl;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.environment:dev}")
    public String environment;

    public PasswordResetService(UserRepository userRepository, JwtServiceImpl jwtServiceImpl,
                                EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtServiceImpl = jwtServiceImpl;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean sendPasswordResetEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            String token = jwtServiceImpl.createPasswordResetToken(email);
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
            String email = jwtServiceImpl.validatePasswordResetToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ServiceException("User not found"));

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (JWTVerificationException e) {
            throw new ServiceException("Invalid or expired token");
        }
    }
}

