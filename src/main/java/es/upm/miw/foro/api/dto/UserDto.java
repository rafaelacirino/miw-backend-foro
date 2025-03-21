package es.upm.miw.foro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.foro.api.dto.validation.CreateValidation;
import es.upm.miw.foro.api.dto.validation.UpdateValidation;
import es.upm.miw.foro.persistance.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(groups = {CreateValidation.class}, message = "First name is required")
    @Size(max = 15, message = "First name must be at most 15 characters")
    private String firstName;

    @NotBlank(groups = {CreateValidation.class}, message = "Last name is required")
    @Size(max = 25, message = "Last name must be at most 25 characters")
    private String lastName;

    @NotBlank(groups = {CreateValidation.class}, message = "Email is required")
    @Email(groups = {CreateValidation.class, UpdateValidation.class}, message = "Invalid email format")
    private String email;

    // @NotNull(message = "Password cannot be null")
    @Size(min = 8, groups = {CreateValidation.class}, message = "Password must be at least 8 characters long")
    //@ValidPassword
    private String password;

    @Schema(hidden = true)
    private Role role;

    @Schema(hidden = true)
    private LocalDateTime registeredDate;
}
