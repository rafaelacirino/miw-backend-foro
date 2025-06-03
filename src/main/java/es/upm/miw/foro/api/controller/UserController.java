package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.LoginDto;
import es.upm.miw.foro.api.dto.TokenDto;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.UserService;
import es.upm.miw.foro.util.ApiPath;
import es.upm.miw.foro.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ToString
@RestController
@RequestMapping(ApiPath.USERS)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "createUser", description = "Create a new User when role is ADMIN and insert into DB")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
        } catch (ServiceException e) {
            HttpStatus status = e.getStatus() != null ? e.getStatus() : HttpStatus.CONFLICT;
            return ResponseEntity.status(status).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "registerUser", description = "Register a new user and insert into DB")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDto userDto) {
        try {
            return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(Map.of(MessageUtil.MESSAGE, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(MessageUtil.MESSAGE, MessageUtil.UNEXPECTED_ERROR));
        }
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "getUserById", description = "Get User by ID")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "login", description = "User login when user is registered and exists in DB")
    public ResponseEntity<Object> login(@RequestBody LoginDto loginDto) {
        try {
            String token = userService.login(loginDto.getEmail(), loginDto.getPassword());
            return ResponseEntity.ok(new TokenDto(token));
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(Map.of(MessageUtil.MESSAGE, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(MessageUtil.MESSAGE, MessageUtil.UNEXPECTED_ERROR));
        }
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "getUsers", description = "Returns all Users based on filters")
    public ResponseEntity<Page<UserDto>> getUsers(@RequestParam(required = false) String firstName,
                                                  @RequestParam(required = false) String lastName,
                                                  @RequestParam(required = false) String email,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "id") String sortBy,
                                                  @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size,
                "desc".equalsIgnoreCase(sortDirection) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        try {
            Page<UserDto> userPage = userService.getAllUsers(firstName, lastName, email, pageable);
            return ResponseEntity.ok(userPage);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "updateUser", description = "Update User into DB")
    public ResponseEntity<Object> updateUser( @Valid @PathVariable Long id,
                                              @RequestBody UserDto userDto) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, userDto));
        } catch (ServiceException e) {
            HttpStatus status = e.getStatus() != null ? e.getStatus() : HttpStatus.CONFLICT;
            return ResponseEntity.status(status).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(MessageUtil.MESSAGE, MessageUtil.UNEXPECTED_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "deleteUser", description = "Delete User by Id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (ServiceException e) {
            if (e.getMessage().equals("User with id " + id + " not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verifyPassword")
    @Operation(summary = "verifyPassword", description = "Verify the password")
    public ResponseEntity<Map<String, Boolean>> verifyPassword(@RequestBody Map<String, String> request) {
        Long userId = Long.valueOf(request.get("userId"));
        String currentPassword = request.get("currentPassword");
        boolean isValid = userService.verifyPassword(userId, currentPassword);

        return ResponseEntity.ok(Map.of("isValid", isValid));
    }
}