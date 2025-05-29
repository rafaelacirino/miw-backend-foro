package es.upm.miw.foro.service.impl;

import es.upm.miw.foro.api.converter.QuestionMapper;
import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.Role;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.QuestionRepository;
import es.upm.miw.foro.persistence.repository.specification.QuestionSpecification;
import es.upm.miw.foro.service.QuestionService;
import es.upm.miw.foro.service.UserService;
import es.upm.miw.foro.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final Validator validator;

    public QuestionServiceImpl(QuestionRepository questionRepository, UserService userService, Validator validator) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.validator = validator;
    }

    @Override
    public QuestionDto createQuestion(QuestionDto questionDto) {
        try {
            validateQuestionDto(questionDto);
            User author = userService.getAuthenticatedUser();
            questionDto.setAnswers(null);
            Question question = QuestionMapper.toEntity(questionDto, author);
            Question savedQuestion = questionRepository.save(question);
            return QuestionMapper.toQuestionDto(savedQuestion);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while saving question", exception);
        } catch (ServiceException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ServiceException("Unexpected error while creating question", exception);
        }
    }

    @Override
    public QuestionDto getQuestionById(Long id) {
        try {
            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new ServiceException(MessageUtil.QUESTION_NOT_FOUND));
            Hibernate.initialize(question.getAuthor());
            Hibernate.initialize(question.getAnswers());
            return QuestionMapper.toQuestionDto(question);
        } catch (DataAccessException e) {
            throw new RepositoryException("Error while retrieving question", e);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while getting question", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDto> getQuestions(String title, boolean unanswered, Pageable pageable) {
        try {
            Page<Question> questionPage;

            if (title != null && !title.isBlank()) {
                questionPage = unanswered
                        ? questionRepository.findByTitleContainingIgnoreCaseAndAnswersEmpty(title, pageable)
                        : questionRepository.findByTitleContainingIgnoreCase(title, pageable);
            } else {
                questionPage = unanswered
                        ? questionRepository.findByAnswersEmpty(pageable)
                        : questionRepository.findAll(pageable);
            }

            questionPage.forEach(question -> {
                Hibernate.initialize(question.getAuthor());
                if (question.getAnswers() != null) {
                    Hibernate.initialize(question.getAnswers());
                }
            });

            return questionPage.map(QuestionMapper::toQuestionDto);
        } catch (DataAccessException exception) {
            log.error("Error while getting filtered questions", exception);
            throw new RepositoryException("Error while getting questions", exception);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDto> searchQuestions(String query, Pageable pageable) {
        try {
            Page<Question> questions = questionRepository.searchByTitleOrDescriptionContainingIgnoreCase(
                    query, pageable);

            List<Question> combinedResults = new ArrayList<>(questions.getContent());

            List<Question> uniqueResults = combinedResults.stream()
                    .distinct()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .toList();

            return new PageImpl<>(
                    uniqueResults.stream().map(QuestionMapper::toQuestionDto).toList(),
                    pageable,
                    questions.getTotalElements());
        } catch (DataAccessException e) {
            throw new RepositoryException("Error searching questions", e);
        }
    }

    @Override
    public QuestionDto updateQuestion(Long id, QuestionDto questionDto) {
        try {
            validateQuestionDto(questionDto);
            User authenticatedUser = userService.getAuthenticatedUser();

            Question existingQuestion = questionRepository.findById(id)
                    .orElseThrow(() -> new ServiceException(MessageUtil.QUESTION_NOT_FOUND_WITH_ID + id));

            if (!existingQuestion.getAuthor().getId().equals(authenticatedUser.getId())) {
                throw new ServiceException("You are not authorized to update this question");
            }
            existingQuestion.setTitle(questionDto.getTitle());
            existingQuestion.setDescription(questionDto.getDescription());

            Question updatedQuestion = questionRepository.save(existingQuestion);
            return QuestionMapper.toQuestionDto(updatedQuestion);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while updating question", exception);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while updating question", e);
        }
    }

    @Override
    public void deleteQuestion(Long id) {
        try {
            User authenticatedUser = userService.getAuthenticatedUser();

            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new ServiceException(MessageUtil.QUESTION_NOT_FOUND_WITH_ID + id));

            if (!question.getAuthor().getId().equals(authenticatedUser.getId()) && !Role.ADMIN.equals(authenticatedUser.getRole())) {
                throw new ServiceException("You are not authorized to delete this question");
            }
            questionRepository.delete(question);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while deleting question", exception);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while deleting question", e);
        }
    }

    @Override
    public boolean isQuestionAuthor(Long questionId, String email) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ServiceException(MessageUtil.QUESTION_NOT_FOUND_WITH_ID + questionId));
        return question.getAuthor().getEmail().equals(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDto> getMyQuestions(String email, String title, LocalDateTime fromDate, Pageable pageable) {
        try {
            Specification<Question> spec = QuestionSpecification.buildQuestionSpecification(email, title, fromDate);
            Page<Question> page = questionRepository.findAll(spec, pageable);
            return page.map(QuestionMapper::toQuestionDto);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error retrieving user questions with filters", e);
        }
    }

    @Transactional
    public void registerView(Long questionId, HttpServletRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RepositoryException(MessageUtil.QUESTION_NOT_FOUND));

        String sessionId = request.getSession().getId();
        User user;
        try {
            user = userService.getAuthenticatedUser();
        } catch (ServiceException ex) {
            user = null;
        }

        Long userId = user != null ? user.getId() : null;
        question.incrementViewsIfNew(sessionId, userId);
    }


    private void validateQuestionDto(QuestionDto dto) {
        Set<ConstraintViolation<QuestionDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new ServiceException(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }
}
