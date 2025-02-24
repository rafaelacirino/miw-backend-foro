package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.LoginRequestDto;
import es.upm.miw.foro.api.dto.TokenDto;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.service.UserService;
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
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public void redirectToSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        String token = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        return ResponseEntity.ok(new TokenDto(token));
    }

    @GetMapping("/getAllUsers")
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
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                              @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id,
                                              @RequestBody UserDto userDto) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
