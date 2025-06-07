package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.EmailDto;
import es.upm.miw.foro.api.dto.ResetPasswordDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.email.PasswordResetService;
import es.upm.miw.foro.util.MessageUtil;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@ToString
@RestController
@RequestMapping("/account")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody EmailDto emailDto) {
        try {
            String email = emailDto.getEmail();
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(MessageUtil.MESSAGE, "Email is required"));
            }

            boolean emailSent = passwordResetService.sendPasswordResetEmail(email);

            if (emailSent) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(MessageUtil.MESSAGE, "If the email exists, a reset link has been sent"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(MessageUtil.MESSAGE, MessageUtil.UNEXPECTED_ERROR));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        try {
            passwordResetService.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(MessageUtil.MESSAGE, MessageUtil.UNEXPECTED_ERROR));
        }
    }
}