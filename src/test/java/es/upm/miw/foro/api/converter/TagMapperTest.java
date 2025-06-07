package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.TagDto;
import es.upm.miw.foro.persistence.model.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class TagMapperTest {

    private static final Long ID = 1L;
    private static final String TAG_NAME = "java";

    @Test
    void toTagDto_shouldMapEntityToDto() {
        // Arrange
        Tag tag = createTag();

        // Act
        TagDto dto = TagMapper.toTagDto(tag);

        // Assert
        assertNotNull(dto);
        assertEquals(tag.getId(), dto.getId());
        assertEquals(tag.getName(), dto.getName());
    }

    @Test
    void toDto_shouldReturnNullWhenEntityIsNull() {
        assertNull(TagMapper.toTagDto(null));
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        // Arrange
        TagDto dto = createTagDto();

        // Act
        Tag tag = TagMapper.toEntity(dto);

        // Assert
        assertNotNull(tag);
        assertEquals(dto.getId(), tag.getId());
        assertEquals(dto.getName(), tag.getName());
    }

    @Test
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        // Act
        Tag tag = TagMapper.toEntity(null);

        // Assert
        assertNull(tag);
    }

    @Test
    void toDtoList_shouldMapList() {
        // Arrange
        List<Tag> tagList = Arrays.asList(createTag(), createTag());

        // Act
        List<TagDto> dtos = TagMapper.toDtoList(tagList);

        // Assert
        assertNotNull(dtos);
        assertEquals(tagList.size(), dtos.size());
    }

    @Test
    void toDtoList_withNullList_shouldReturnEmpty() {
        List<TagDto> dtos = TagMapper.toDtoList(null);
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void toEntityList_shouldMapList() {
        // Arrange
        List<TagDto> dtos = Arrays.asList(createTagDto(), createTagDto());

        // Act
        List<Tag> tagList = TagMapper.toEntityList(dtos);

        // Assert
        assertNotNull(tagList);
        assertEquals(dtos.size(), tagList.size());
    }

    @Test
    void toEntityList_withNullList_shouldReturnEmpty() {
        List<Tag> tags = TagMapper.toEntityList(null);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }

    private Tag createTag() {
        Tag tag = new Tag();
        tag.setId(ID);
        tag.setName(TAG_NAME);

        return tag;
    }

    private TagDto createTagDto() {
        TagDto dto = new TagDto();
        dto.setId(ID);
        dto.setName(TAG_NAME);

        return dto;
    }
}
