package es.upm.miw.foro.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.service.impl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestConfig
class JwtAuthenticationFilterTest {

    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "mockedToken";
    private static final String BEARER_TOKEN = "Bearer " + TOKEN;
    private static final String ROLE = "GUEST";
    private static final String USERNAME = "mockUser";
    private static final String PASSWORD = "mockPassword";

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
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @Test
    void testDoFilterInternal_WithNoToken() throws ServletException, IOException {
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void testDoFilterInternal_WithInvalidBearerToken() throws ServletException, IOException {
        // Arrange
        request.addHeader(AUTHORIZATION, BEARER_TOKEN);
        when(jwtService.extractToken(BEARER_TOKEN)).thenReturn(TOKEN);
        when(jwtService.verify(TOKEN)).thenReturn(Optional.empty());

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractToken(BEARER_TOKEN);
        verify(jwtService, times(1)).verify(TOKEN);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void testDoFilterInternal_WithValidTokenAndNoExistingAuth() throws ServletException, IOException {
        // Arrange
        request.addHeader(AUTHORIZATION, BEARER_TOKEN);
        when(jwtService.extractToken(BEARER_TOKEN)).thenReturn(TOKEN);
        when(jwtService.verify(TOKEN)).thenReturn(Optional.of(mock(DecodedJWT.class)));
        when(jwtService.user(TOKEN)).thenReturn(USERNAME);
        when(jwtService.role(TOKEN)).thenReturn(ROLE);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isInstanceOf(UserDetails.class);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assertThat(userDetails.getUsername()).isEqualTo(USERNAME);
        assertThat(authentication.getCredentials()).isNull();
        assertThat(authentication.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_" + ROLE);
        assertThat(authentication.getDetails()).isNotNull();

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractToken(BEARER_TOKEN);
        verify(jwtService, times(1)).verify(TOKEN);
        verify(jwtService, times(1)).user(TOKEN);
        verify(jwtService, times(1)).role(TOKEN);
    }

    @Test
    void testDoFilterInternal_WithValidTokenAndExistingAuth() throws ServletException, IOException {
        // Arrange
        request.addHeader(AUTHORIZATION, BEARER_TOKEN);
        when(jwtService.extractToken(BEARER_TOKEN)).thenReturn(TOKEN);
        when(jwtService.verify(TOKEN)).thenReturn(Optional.of(mock(com.auth0.jwt.interfaces.DecodedJWT.class)));
        when(jwtService.user(TOKEN)).thenReturn(USERNAME);

        UserDetails existingUser = User.withUsername(USERNAME).password(PASSWORD).roles(String.valueOf(Role.MEMBER)).build();
        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken(
                existingUser, null, existingUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isSameAs(existingAuth);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractToken(BEARER_TOKEN);
        verify(jwtService, times(1)).verify(TOKEN);
        verify(jwtService, times(1)).user(TOKEN);
        verify(jwtService, never()).role(anyString());
    }
}

