package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@TestConfig
class SwaggerRedirectControllerTest {

    @InjectMocks
    private SwaggerRedirectController swaggerRedirectController;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRedirectToSwagger() throws IOException {
        swaggerRedirectController.redirectToSwagger(response);
        verify(response).sendRedirect("/swagger-ui/index.html");
    }
}
