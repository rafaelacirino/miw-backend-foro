package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.LoginDto;
import es.upm.miw.foro.api.dto.TokenDto;
import es.upm.miw.foro.api.dto.UserDto;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "createUser", description = "Create a new User when role is ADMIN and insert into DB")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        try {
            return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "registerUser", description = "Register a new user and insert into DB")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
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
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            String token = userService.login(loginDto.getEmail(), loginDto.getPassword());
            return ResponseEntity.ok(new TokenDto(token));
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/getAllUsers")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
        try {
            Page<UserDto> userPage = userService.getAllUsers(firstName, lastName, email, pageable);
            return ResponseEntity.ok(userPage);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "updateUser", description = "Update User into DB")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                              @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/delete/{id}")
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
}