package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.AnswerService;
import es.upm.miw.foro.util.ApiPath;
import es.upm.miw.foro.util.StatusMsg;
import jakarta.validation.Valid;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public ResponseEntity<AnswerDto> createAnswer(@Valid @RequestBody AnswerDto answerDto) {
        try {
            return new ResponseEntity<>(answerService.createAnswer(answerDto), HttpStatus.CREATED);
        } catch (ServiceException e) {
            if (e.getMessage().contains(StatusMsg.UNAUTHORIZED)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
