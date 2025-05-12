package es.upm.miw.foro.service.impl;

import com.google.api.gax.rpc.NotFoundException;
import es.upm.miw.foro.api.converter.AnswerMapper;
import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.Answer;
import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.AnswerRepository;
import es.upm.miw.foro.persistance.repository.QuestionRepository;
import es.upm.miw.foro.persistance.repository.UserRepository;
import es.upm.miw.foro.service.AnswerService;
import es.upm.miw.foro.service.NotificationService;
import es.upm.miw.foro.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final Validator validator;

    public AnswerServiceImpl(AnswerRepository answerRepository, QuestionRepository questionRepository,
                             UserRepository userRepository, UserService userService, NotificationService notificationService, Validator validator) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.validator = validator;
    }

    @Override
    public AnswerDto createAnswer(AnswerDto answerDto) {
        try {
            validateAnswerDto(answerDto);
            System.out.println("Validating user...");
            User author = userService.getAuthenticatedUser();
            System.out.println("Validate user...");
            Question question = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new ServiceException("Question not found"));

            Answer answer = AnswerMapper.toEntity(answerDto, question);
            answer.setAuthor(author);
            Answer savedAnswer = answerRepository.save(answer);

            if (!question.getAuthor().getId().equals(author.getId())) {
                System.out.println("Sending notification...");
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
