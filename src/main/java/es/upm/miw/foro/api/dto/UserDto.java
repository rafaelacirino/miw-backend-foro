package es.upm.miw.foro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.foro.persistance.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Schema(hidden = true)
    private Long id;

    @NotNull(message = "First name cannot be null")
    @Size(max = 15, message = "First name must be at most 15 characters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Size(max = 25, message = "Last name must be at most 25 characters")
    private String lastName;

    @NotNull(message = "Email cannot be null")
    @Size(max = 20, message = "Email must be at most 20 characters")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min =6, max = 12, message = "Password must be between 6 and 12 characters")
    //@ValidPassword
    private String password;

    @Size(max = 9, message = "Phone must be at most 9 characters")
    private String phone;

    @Size(max = 25, message = "Address must be at most 25 characters")
    private String address;

    private Role role;

    private LocalDateTime registeredDate;
}
