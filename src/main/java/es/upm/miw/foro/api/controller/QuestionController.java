package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.QuestionService;
import es.upm.miw.foro.util.ApiPath;
import es.upm.miw.foro.util.MessageUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@ToString
@Slf4j
@RestController
@RequestMapping(ApiPath.QUESTIONS)
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<QuestionDto> createQuestion(@Valid @RequestBody QuestionDto questionDto) {
        try {
            return new ResponseEntity<>(questionService.createQuestion(questionDto),HttpStatus.CREATED);
        } catch (ServiceException e) {
            if (e.getMessage().contains(MessageUtil.UNAUTHORIZED)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(@Valid @PathVariable Long id) {
        try {
            QuestionDto questionDto = questionService.getQuestionById(id);

            if (questionDto == null) {
                log.warn("Question mapping resulted in null DTO for id: {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            return ResponseEntity.ok(questionDto);
        } catch (ServiceException e) {
            log.warn("Question not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error retrieving question with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<QuestionDto>> searchQuestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<QuestionDto> results = questionService.searchQuestions(query, PageRequest.of(page, size));
            return ResponseEntity.ok(results);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<QuestionDto>> getQuestions(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

       try {
           Pageable pageable = PageRequest.of(page, size, "desc".equalsIgnoreCase(sortDirection) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
           Page<QuestionDto> questionPage = questionService.getQuestions(title, pageable);
           return ResponseEntity.ok(questionPage);
       } catch (ServiceException e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
       }
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable Long id,
                                                      @RequestBody QuestionDto questionDto) {
        try {
            QuestionDto updateQuestion = questionService.updateQuestion(id, questionDto);
            return ResponseEntity.ok(updateQuestion);
        } catch (ServiceException e) {
            if (e.getMessage().contains(MessageUtil.UNAUTHORIZED)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or @questionServiceImpl.isQuestionAuthor(#id, authentication.name)")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.noContent().build();
        } catch (ServiceException e) {
            if (e.getMessage().contains(MessageUtil.UNAUTHORIZED) || e.getMessage().contains("authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Unexpected error while deleting question", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<Page<QuestionDto>> getMyQuestions(
            Authentication authentication,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            String email = authentication.getName();
            Pageable pageable = PageRequest.of(page, size,
                    "desc".equalsIgnoreCase(sortDirection) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
            );
            Page<QuestionDto> myQuestions = questionService.getMyQuestions(email, title, fromDate, pageable);
            return ResponseEntity.ok(myQuestions);
        } catch (Exception e) {
            log.error("Error getting questions for current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/{id}/views")
    public ResponseEntity<Void> registerView(@PathVariable Long id, HttpServletRequest request) {
        questionService.registerView(id, request);
        return ResponseEntity.noContent().build();
    }
}
