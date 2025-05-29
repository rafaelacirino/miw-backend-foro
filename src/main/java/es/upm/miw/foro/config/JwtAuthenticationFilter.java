package es.upm.miw.foro.config;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import es.upm.miw.foro.service.impl.JwtServiceImpl;
import es.upm.miw.foro.util.ApiPath;
import es.upm.miw.foro.util.MessageUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtServiceImpl jwtServiceImpl;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws IOException, ServletException {
        String path = request.getServletPath();
        String method = request.getMethod();
        log.info("Request path: {}", path);
        if (("GET".equalsIgnoreCase(method)) && (path.equals(ApiPath.QUESTIONS) ||
                path.matches(ApiPath.QUESTIONS + "/\\d+") ||
                path.equals(ApiPath.QUESTION_SEARCH))) {
            chain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader(MessageUtil.AUTHORIZATION);
        log.info("JWT Header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = jwtServiceImpl.extractToken(authHeader);
        log.info("Extracted Token: {}", token);
        try {
            Optional<DecodedJWT> decodedJWT = jwtServiceImpl.verify(token);
            log.info("DecodedJWT: {}", decodedJWT.isPresent());
            if (decodedJWT.isEmpty()) {
                chain.doFilter(request, response);
                return;
            }
            String userEmail = jwtServiceImpl.user(token);
            log.info("User: {}", userEmail);
            if (userEmail == null || userEmail.isEmpty()) {
                log.error("Failed to extract user email from token");
                chain.doFilter(request, response);
                return;
            }
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = loadUserByUsername(token, userEmail);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JWTDecodeException e) {
            log.error("Failed to decode token: {}", e.getMessage());
            chain.doFilter(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

    private UserDetails loadUserByUsername(String token, String userEmail) {
        String role = jwtServiceImpl.role(token);
        log.info("Role for user {}: {}", userEmail, role);
        return org.springframework.security.core.userdetails.User.builder()
                .username(userEmail)
                .password("")
                .roles(role)
                .build();
    }
}
