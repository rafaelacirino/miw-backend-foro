package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.LoginDto;
import es.upm.miw.foro.api.dto.TokenDto;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.api.dto.validation.CreateValidation;
import es.upm.miw.foro.api.dto.validation.UpdateValidation;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    @Operation(summary = "redirectToSwagger", description = "Method to redirect to Swagger in production")
    public void redirectToSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    @Operation(summary = "createUser", description = "Create a new User when role is ADMIN and insert into DDBB")
    public ResponseEntity<UserDto> createUser(@Validated(CreateValidation.class) @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @PostMapping("/register")
    @Operation(summary = "registerUser", description = "Register a new user and insert into DDBB")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "getUserById", description = "Get User by ID")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "getAuthenticatedUser", description = "Get details of the authenticated user")
    public ResponseEntity<UserDto> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = authentication.getName();

        try {
            UserDto userDto = userService.getUserByEmail(email);
            return ResponseEntity.ok(userDto);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "login", description = "User login when user is registered and exists in DDBB")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            String token = userService.login(loginDto.getEmail(), loginDto.getPassword());
            return ResponseEntity.ok(new TokenDto(token));
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/getAllUsers")
    @Operation(summary = "getAllUsers", description = "Returns all Users based on filters")
    public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam(required = false) String firstName,
                                                     @RequestParam(required = false) String lastName,
                                                     @RequestParam(required = false) String email,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(defaultValue = "id") String sortBy,
                                                     @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size,
                "desc".equalsIgnoreCase(sortDirection) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<UserDto> userPage = userService.getAllUsers(firstName, lastName, email, pageable);

        return ResponseEntity.ok(userPage);
    }

    @PutMapping("/{id}")
    @Operation(summary = "updateUser", description = "Update User into DDBB")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                              @Validated(UpdateValidation.class) @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "deleteUser", description = "Delete User by Id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build(); // 204 No Content
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
