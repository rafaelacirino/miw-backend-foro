package es.upm.miw.foro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.foro.persistance.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;

    @NotNull(message = "First name cannot be null")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    private String lastName;

    @NotNull(message = "Email cannot be null")
    private String email;

    @NotNull(message = "Password cannot be null")
    private String password;

    private String phone;

    private String address;

    private Role role;

    private LocalDateTime registeredDate;
}
