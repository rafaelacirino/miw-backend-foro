package es.upm.miw.foro.service.impl;

import es.upm.miw.foro.api.converter.TagMapper;
import es.upm.miw.foro.api.dto.TagDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Tag;
import es.upm.miw.foro.persistence.repository.TagRepository;
import es.upm.miw.foro.service.TagService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

import java.util.Set;
import java.util.stream.Collectors;

public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final Validator validator;

    public TagServiceImpl(TagRepository tagRepository, Validator validator) {
        this.tagRepository = tagRepository;
        this.validator = validator;
    }

    @Override
    public TagDto createTag(TagDto tagDto) {
        try {
            validateTagDto(tagDto);
            Tag tag = TagMapper.toEntity(tagDto);
            Tag savedTag = tagRepository.save(tag);
            return TagMapper.toTagDto(savedTag);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while saving tag", exception);
        } catch (ServiceException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ServiceException("Unexpected error while creating tag", exception);
        }
    }


    private void validateTagDto(TagDto dto) {
        Set<ConstraintViolation<TagDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new ServiceException(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }
}
