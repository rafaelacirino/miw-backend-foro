package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.AnswerService;
import es.upm.miw.foro.util.ApiPath;
import es.upm.miw.foro.util.MessageUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import java.util.List;

@ToString
@Slf4j
@RestController
@RequestMapping(ApiPath.ANSWERS)
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping()
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<AnswerDto> createAnswer(@RequestParam Long questionId,
                                                  @Valid @RequestBody AnswerDto answerDto) {
        try {
            return new ResponseEntity<>(answerService.createAnswer(questionId, answerDto), HttpStatus.CREATED);
        } catch (ServiceException e) {
            if (e.getMessage().contains(MessageUtil.UNAUTHORIZED)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping()
    public ResponseEntity<List<AnswerDto>> getAnswersByQuestion(@RequestParam Long questionId) {
        try {
            List<AnswerDto> answers = answerService.getAnswersByQuestionId(questionId);
            return ResponseEntity.ok(answers);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnswerDto> getAnswerById(@Valid @PathVariable Long id) {
        try {
            AnswerDto answerDto = answerService.getAnswerById(id);

            if (answerDto == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.ok(answerDto);
        } catch (ServiceException e) {
            log.warn(MessageUtil.ANSWER_NOT_FOUND, id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error retrieving answer with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/myAnswers")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<Page<AnswerDto>> getMyAnswers(
            Authentication authentication,
            @RequestParam(required = false) String question,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) LocalDateTime creationDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            String email = authentication.getName();
            Pageable pageable = PageRequest.of(page, size, "desc".equalsIgnoreCase(sortDirection) ?
                                                            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
            Page<AnswerDto> myAnswers = answerService.getMyAnswers(email, question, content, creationDate, pageable);
            return ResponseEntity.ok(myAnswers);
        } catch (Exception e) {
            log.error("Error getting questions for current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<AnswerDto> updateAnswer(@PathVariable Long id,
                                                  @RequestBody AnswerDto answerDto) {
        try {
            AnswerDto updatedAnswer = answerService.updateAnswer(id, answerDto);
            return ResponseEntity.ok(updatedAnswer);
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
    @PreAuthorize("hasRole('ADMIN') or @answerServiceImpl.isAnswerAuthor(#id, authentication.name)")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        try {
            answerService.deleteAnswer(id);
            return ResponseEntity.noContent().build();
        } catch (ServiceException e) {
            if (e.getMessage().contains(MessageUtil.UNAUTHORIZED) || e.getMessage().contains("authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
