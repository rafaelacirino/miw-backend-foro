package es.upm.miw.foro.service;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.TagDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Role;
import es.upm.miw.foro.persistence.model.Tag;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.TagRepository;
import es.upm.miw.foro.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestConfig
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TagServiceImpl tagService;

    private User admin;
    private User member;
    private Tag tag;

    private static final String NAME = "java";
    private static final String SEARCH_NAME = "jav";

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);

        member = new User();
        member.setId(2L);
        member.setEmail("user@test.com");
        member.setRole(Role.MEMBER);

        tag = new Tag();
        tag.setId(1L);
        tag.setName(NAME);
        tag.setQuestions(new ArrayList<>());
    }

    @Test
    void testGetAllTags_success() {
        // Arrange
        when(tagRepository.findAll()).thenReturn(List.of(tag));

        // Act
        List<TagDto> result = tagService.getAllTags();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(NAME, result.getFirst().getName());

        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void testSearchTags_success() {
        // Arrange
        when(tagRepository.findByNameContainingIgnoreCase(SEARCH_NAME)).thenReturn(List.of(tag));

        // Act
        List<TagDto> result = tagService.searchTags(SEARCH_NAME);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(NAME, result.getFirst().getName());

        verify(tagRepository, times(1)).findByNameContainingIgnoreCase(SEARCH_NAME);
    }

    @Test
    void testSearchTags_emptyQuery() {
        // Act
        List<TagDto> result = tagService.searchTags("   ");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(tagRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void testSearchTags_dataAccessException() {
        // Arrange
        when(tagRepository.findByNameContainingIgnoreCase(SEARCH_NAME))
                .thenThrow(new DataAccessException("DB error") {});

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> tagService.searchTags(SEARCH_NAME));

        assertEquals("Error searching tags", exception.getMessage());

        verify(tagRepository, times(1)).findByNameContainingIgnoreCase(SEARCH_NAME);
    }

    @Test
    void testDeleteTag() {
        // Arrange
        tag.setQuestions(new ArrayList<>());

        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);

        // Act
        assertDoesNotThrow(() -> tagService.deleteTag(1L));

        // Verify
        verify(userService, times(1)).getAuthenticatedUser();
        verify(tagRepository, times(1)).findById(1L);
        verify(tagRepository, times(1)).delete(tag);
    }

    @Test
    void testDeleteTag_notFound() {
        // Arrange
        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> tagService.deleteTag(1L));

        assertEquals("Tag not found with id: 1", exception.getMessage());

        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteTag_unauthorized() {
        // Arrange
        when(userService.getAuthenticatedUser()).thenReturn(member);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> tagService.deleteTag(1L));

        assertEquals("Only admins can delete tags", exception.getMessage());

        verify(userService, times(1)).getAuthenticatedUser();
        verify(tagRepository, never()).findById(any());
        verify(tagRepository, never()).delete(any());
    }
}
