package es.upm.miw.foro.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResetPasswordDto {

    private String token;
    private String newPassword;
}
