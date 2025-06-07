package es.upm.miw.foro.service.impl;

import com.google.api.gax.rpc.NotFoundException;
import es.upm.miw.foro.api.converter.AnswerMapper;
import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.Role;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.AnswerRepository;
import es.upm.miw.foro.persistence.repository.QuestionRepository;
import es.upm.miw.foro.persistence.repository.specification.AnswerSpecification;
import es.upm.miw.foro.service.AnswerService;
import es.upm.miw.foro.service.NotificationService;
import es.upm.miw.foro.service.UserService;
import es.upm.miw.foro.util.MessageUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final Validator validator;

    public AnswerServiceImpl(AnswerRepository answerRepository, QuestionRepository questionRepository,
                             UserService userService, NotificationService notificationService, Validator validator) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.validator = validator;
    }

    @Override
    public AnswerDto createAnswer(Long questionId, AnswerDto answerDto) {
        try {
            validateAnswerDto(answerDto);
            log.info("Validating user...");
            User author = userService.getAuthenticatedUser();
            log.info("Validate user...");
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ServiceException(MessageUtil.QUESTION_NOT_FOUND));

            Answer answer = AnswerMapper.toEntity(answerDto, question, author);
            Answer savedAnswer = answerRepository.save(answer);

            if (!question.getAuthor().getId().equals(author.getId())) {
                log.info("Sending notification...");
                notificationService.notifyNewAnswer(question.getAuthor(), question, savedAnswer);
            }

            return AnswerMapper.toAnswerDto(savedAnswer);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while saving answer", exception);
        } catch (NotFoundException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ServiceException("Unexpected error while creating answer", exception);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerDto> getAnswersByQuestionId(Long questionId) {
        try {
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RepositoryException(MessageUtil.QUESTION_NOT_FOUND));

            List<Answer> answers = answerRepository.findByQuestionOrderByCreationDateAsc(question);
            return AnswerMapper.toDtoList(answers);

        } catch (DataAccessException e) {
            throw new RepositoryException("Error fetching answers", e);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error fetching answers", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerDto getAnswerById(Long answerId) {
        try {
            Answer answer = answerRepository.findById(answerId)
                    .orElseThrow(() -> new RepositoryException(MessageUtil.ANSWER_NOT_FOUND + answerId));
            return AnswerMapper.toAnswerDto(answer);
        } catch (DataAccessException e) {
            throw new RepositoryException("Error while retrieving answer", e);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while getting answer", e);
        }
    }

    @Override
    @Transactional
    public AnswerDto updateAnswer(Long id, AnswerDto answerDto) {
        try {
            validateAnswerDto(answerDto);
            User authenticateUser = userService.getAuthenticatedUser();

            Answer existingAnswer = answerRepository.findByIdWithAuthor(id)
                    .orElseThrow(() -> new ServiceException(MessageUtil.ANSWER_NOT_FOUND + id));

            if (!existingAnswer.getAuthor().getId().equals(authenticateUser.getId())) {
                throw new ServiceException("You are not authorized to update this answer");
            }
            existingAnswer.setContent(answerDto.getContent());
            Answer updatedAnswer = answerRepository.save(existingAnswer);
            Answer reloadedAnswer = answerRepository.findByIdWithAuthor(updatedAnswer.getId())
                            .orElseThrow(() -> new ServiceException("Updated answer not found"));
            return AnswerMapper.toAnswerDto(reloadedAnswer);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while updating answer", exception);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while updating answer", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnswerDto> getMyAnswers(String email, String question, String content, LocalDateTime creationDate, Pageable pageable) {
        try {
            Specification<Answer> spec = AnswerSpecification.buildAnswerSpecification(email, question, content, creationDate);
            Page<Answer> page = answerRepository.findAll(spec, pageable);
            return page.map(AnswerMapper::toAnswerDto);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error retrieving user answers with filters", e);
        }
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id) {
        try {
            User authenticateUser = userService.getAuthenticatedUser();

            Answer answer = answerRepository.findByIdWithAuthor(id)
                    .orElseThrow(() -> new ServiceException(MessageUtil.ANSWER_NOT_FOUND + id));

            if (!answer.getAuthor().getId().equals(authenticateUser.getId()) && !Role.ADMIN.equals(authenticateUser.getRole())) {
                throw new ServiceException("You are not authorized to delete this answer");
            }

            notificationService.deleteByAnswerId(answer.getId());
            answerRepository.delete(answer);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while deleting answer", exception);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while deleting answer", e);
        }
    }

    @Override
    @Transactional
    public boolean isAnswerAuthor(Long answerId, String email) {
        Answer answer = answerRepository.findByIdWithAuthor(answerId)
                .orElseThrow(() -> new ServiceException(MessageUtil.ANSWER_NOT_FOUND + answerId));
        return answer.getAuthor().getEmail().equals(email);
    }

    private void validateAnswerDto(AnswerDto dto) {
        Set<ConstraintViolation<AnswerDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new ServiceException(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }
}
