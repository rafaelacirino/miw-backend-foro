package es.upm.miw.foro.config;

import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.service.impl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private JwtService jwtService;

//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain chain)
//            throws IOException, ServletException {
//        String token = jwtService.extractToken(request.getHeader(AUTHORIZATION));
//        if (token != null && !token.isEmpty()) {
//            GrantedAuthority authority = new SimpleGrantedAuthority(Role.PREFIX + jwtService.role(token));
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(jwtService.user(token), token, List.of(authority));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        chain.doFilter(request, response);
//    }
@Override
protected void doFilterInternal(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull FilterChain chain)
        throws IOException, ServletException {

    try {
        String bearer = request.getHeader(AUTHORIZATION);
        String token = jwtService.extractToken(bearer);

        if (token != null && !token.isEmpty()) {
            String email = jwtService.user(token);
            String role = jwtService.role(token);

            if (!email.isEmpty()) {
                GrantedAuthority authority = new SimpleGrantedAuthority(Role.PREFIX + role);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    } catch (Exception e) {
    }

    chain.doFilter(request, response);
}

}
