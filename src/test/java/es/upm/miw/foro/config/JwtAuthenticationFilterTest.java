package es.upm.miw.foro.config;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.service.impl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestConfig
class JwtAuthenticationFilterTest {

    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "mockedToken";
    private static final String ROLE = "USER";
    private static final String USERNAME = "mockUser";

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @Test
    void testDoFilterInternal_WithValidToken() throws ServletException, IOException {
        request.addHeader(AUTHORIZATION, TOKEN);
        when(jwtService.extractToken(TOKEN)).thenReturn(TOKEN);
        when(jwtService.role(TOKEN)).thenReturn(ROLE);
        when(jwtService.user(TOKEN)).thenReturn(USERNAME);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isEqualTo(USERNAME);
        assertThat(authentication.getCredentials()).isEqualTo(TOKEN);
        assertThat(authentication.getAuthorities()).extracting("authority").contains("ROLE_" + ROLE);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithNoToken() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}

