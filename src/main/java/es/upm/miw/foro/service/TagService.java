package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.TagDto;

import java.util.List;

public interface TagService {

    List<TagDto> getAllTags();

    List<TagDto> searchTags(String query);

    void deleteTag(Long tagId);
}
