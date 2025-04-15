package es.upm.miw.foro.service.impl;

import es.upm.miw.foro.api.converter.QuestionMapper;
import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.QuestionRepository;
import es.upm.miw.foro.service.QuestionService;
import es.upm.miw.foro.service.UserService;
import org.hibernate.Hibernate;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserService userService;

    public QuestionServiceImpl(QuestionRepository questionRepository, UserService userService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
    }

    @Override
    public QuestionDto createQuestion(QuestionDto questionDto) {
        try {
            User author = userService.getAuthenticatedUser();
            questionDto.setAnswers(null);
            Question question = QuestionMapper.toEntity(questionDto, author);
            Question savedQuestion = questionRepository.save(question);
            return QuestionMapper.toQuestionDto(savedQuestion);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while saving question", exception);
        } catch (Exception exception) {
            throw new ServiceException("Unexpected error while creating question", exception);
        }
    }

    @Override
    public QuestionDto getQuestionById(Long id) {
        try {
            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new ServiceException("Question not found with id: " + id));
            return QuestionMapper.toQuestionDto(question);
        } catch (DataAccessException e) {
            throw new RepositoryException("Error while retrieving question", e);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while getting question", e);
        }
    }

    @Override
    public List<QuestionDto> getQuestionByTitle(String title) {
        try {
            List<Question> questions = questionRepository.findByTitleContainingIgnoreCase(title);
            return questions.stream().map(QuestionMapper::toQuestionDto).collect(Collectors.toList());
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while getting question with title: " + title, exception);
        }
    }

    @Override
    public Page<QuestionDto> getAllQuestions(String title, Pageable pageable) {
        try {
            Page<Question> questionPage;
            if (title != null && !title.isBlank()) {
                questionPage = questionRepository.findByTitleContainingIgnoreCase(title, pageable);
            } else {
                questionPage = questionRepository.findAll(pageable);
            }
            questionPage.forEach(question ->
                Hibernate.initialize(question.getAuthor()));
            return questionPage.map(QuestionMapper::toQuestionDto);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while getting questions", exception);
        }
    }

    @Override
    public QuestionDto updateQuestion(Long id, QuestionDto questionDto) {
        try {
            User authenticatedUser = userService.getAuthenticatedUser();

            Question existingQuestion = questionRepository.findById(id)
                    .orElseThrow(() -> new ServiceException("Question not found with id: " + id));

            if (!existingQuestion.getAuthor().getId().equals(authenticatedUser.getId())) {
                throw new ServiceException("You are not authorized to update this question");
            }
            existingQuestion.setTitle(questionDto.getTitle());
            existingQuestion.setDescription(questionDto.getDescription());

            Question updatedQuestion = questionRepository.save(existingQuestion);
            return QuestionMapper.toQuestionDto(updatedQuestion);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while updating question", exception);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while updating question", e);
        }
    }

    @Override
    public boolean isQuestionAuthor(Long questionId, String username) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ServiceException("Question not found with id: " + questionId));
        return question.getAuthor().getUserName().equals(username);
    }

    @Override
    public void deleteQuestion(Long id) {
        try {
            User authenticatedUser = userService.getAuthenticatedUser();

            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new ServiceException("Question not found with id: " + id));

            if (!question.getAuthor().getId().equals(authenticatedUser.getId()) && !Role.ADMIN.equals(authenticatedUser.getRole())) {
                throw new ServiceException("You are not authorized to delete this question");
            }
            questionRepository.delete(question);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error while deleting question", exception);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while deleting question", e);
        }
    }
}
