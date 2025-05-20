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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ToString
@Slf4j
@RestController
@RequestMapping(ApiPath.QUESTIONS)
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/{questionId}/answers")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<AnswerDto> createAnswer(@PathVariable Long questionId,
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

    @GetMapping("/{questionId}/answers")
    public ResponseEntity<List<AnswerDto>> getAnswersByQuestion(@PathVariable Long questionId) {
        try {
            List<AnswerDto> answers = answerService.getAnswersByQuestionId(questionId);
            return ResponseEntity.ok(answers);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
