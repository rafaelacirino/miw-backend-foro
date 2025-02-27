package es.upm.miw.foro.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginDto {

    private String email;
    private String password;
}
