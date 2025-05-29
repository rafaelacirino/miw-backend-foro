package es.upm.miw.foro.service.impl;

import es.upm.miw.foro.api.converter.TagMapper;
import es.upm.miw.foro.api.dto.TagDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Role;
import es.upm.miw.foro.persistence.model.Tag;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.TagRepository;
import es.upm.miw.foro.service.TagService;
import es.upm.miw.foro.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserService userService;

    public TagServiceImpl(TagRepository tagRepository, UserService userService) {
        this.tagRepository = tagRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> searchTags(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return Collections.emptyList();
            }

            List<Tag> tags = tagRepository.findByNameContainingIgnoreCase(query.trim());
            return TagMapper.toDtoList(tags);
        } catch (DataAccessException e) {
            throw new RepositoryException("Error searching tags", e);
        }
    }

    @Override
    @Transactional
    public void deleteTag(Long tagId) {
        User authenticatedUser = userService.getAuthenticatedUser();

        if (!authenticatedUser.getRole().equals(Role.ADMIN)) {
            throw new ServiceException("Only admins can delete tags");
        }

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ServiceException("Tag not found with id: " + tagId));

        tag.getQuestions().forEach(q -> q.getTags().remove(tag));
        tagRepository.delete(tag);
    }
}
