package es.upm.miw.foro;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class ApplicationMainTest {

	@Test
	void applicationContextLoads() {
		try (MockedStatic<SpringApplication> mockedStatic = Mockito.mockStatic(SpringApplication.class)) {
			Application.main(new String[]{});
			mockedStatic.verify(() -> SpringApplication.run(Application.class, new String[]{}));
		}
	}
}
