package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.TagDto;
import es.upm.miw.foro.service.TagService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestConfig
class TagControllerTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    private static final Long ID = 1L;

    private final TagDto tagDto = new TagDto(ID, "java");

    @Test
    void testGetTags() {
        // Arrange
        List<TagDto> tags = List.of(tagDto);
        when(tagService.getAllTags()).thenReturn(tags);

        // Act
        ResponseEntity<List<TagDto>> response = tagController.getAllTags();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tagService, times(1)).getAllTags();
    }

    @Test
    void testSearchTags() {
        // Arrange
        String query = "java";
        List<TagDto> results = List.of(tagDto);
        when(tagService.searchTags(query)).thenReturn(results);

        // Act
        ResponseEntity<List<TagDto>> response = tagController.searchTags(query);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tagService, times(1)).searchTags(query);
    }

    @Test
    void testDeleteTag() {
        // Act
        ResponseEntity<Void> response = this.tagController.deleteTag(ID);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(tagService, times(1)).deleteTag(ID);
    }
}
