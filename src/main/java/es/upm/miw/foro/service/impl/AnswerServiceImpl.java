package es.upm.miw.foro.service.impl;

import com.google.api.gax.rpc.NotFoundException;
import es.upm.miw.foro.api.converter.AnswerMapper;
import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.AnswerRepository;
import es.upm.miw.foro.persistence.repository.QuestionRepository;
import es.upm.miw.foro.service.AnswerService;
import es.upm.miw.foro.service.NotificationService;
import es.upm.miw.foro.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                    .orElseThrow(() -> new ServiceException("Question not found"));

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
                    .orElseThrow(() -> new RepositoryException("Question not found"));

            List<Answer> answers = answerRepository.findByQuestionOrderByCreationDateAsc(question);
            return AnswerMapper.toDtoList(answers);

        } catch (DataAccessException e) {
            throw new RepositoryException("Error fetching answers", e);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error fetching answers", e);
        }
    }

    @Override
    public AnswerDto updateAnswer(Long questionId, AnswerDto answerDto) {
        return null;
    }

    @Override
    public Page<AnswerDto> getMyAnswers(String author, String content, LocalDateTime creationDate, Pageable pageable) {
        return null;
    }

    @Override
    public void deleteAnswer(Long id) {

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
