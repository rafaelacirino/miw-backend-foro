package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.TagDto;
import es.upm.miw.foro.persistence.model.Tag;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class TagMapper {

    @Generated
    private TagMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static TagDto toTagDto(Tag tag) {
        if (tag == null) {
            return null;
        }

        TagDto tagDto = new TagDto();
        populateDto(tag, tagDto);
        return tagDto;
    }

    public static Tag toEntity(TagDto tagDto) {
        if (tagDto == null) {
            return null;
        }
        Tag tag = new Tag();
        populateEntity(tag, tagDto);
        return tag;
    }

    public static List<TagDto> toDtoList(List<Tag> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags.stream()
                .map(TagMapper::toTagDto)
                .toList();
    }

    public static List<Tag> toEntityList(List<TagDto> tagDtos) {
        if (tagDtos == null) {
            return Collections.emptyList();
        }
        return tagDtos.stream()
                .map(TagMapper::toEntity)
                .toList();
    }

    private static void populateDto(Tag tag, TagDto dto) {
        if (tag == null || dto == null) {
            return;
        }
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setQuestionCount(tag.getQuestions() != null ? tag.getQuestions().size() : 0);
    }

    private static void populateEntity(Tag tag, TagDto tagDto) {
        tag.setId(tagDto.getId());
        tag.setName(tagDto.getName());
    }
}
