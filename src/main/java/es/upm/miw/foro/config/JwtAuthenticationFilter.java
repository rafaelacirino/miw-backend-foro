package es.upm.miw.foro.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import es.upm.miw.foro.service.impl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws IOException, ServletException {
        String authHeader = request.getHeader(AUTHORIZATION);
        log.info("JWT: {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = jwtService.extractToken(authHeader);
        log.info("JWT: {}", token);
        Optional<DecodedJWT> decodedJWT = jwtService.verify(token);
        log.info("DecodedJWT: {}", decodedJWT.isPresent());
        if (decodedJWT.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        String userEmail = jwtService.user(token);
        log.info("User: {}", userEmail);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = loadUserByUsername(userEmail);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private UserDetails loadUserByUsername(String email) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(email)
                .password("")
                .roles(jwtService.role(email))
                .build();
    }
}
