package br.edu.streamingplatform.user.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.streamingplatform.user.dto.UserDto;
import br.edu.streamingplatform.user.dto.UserExistsResponse;
import br.edu.streamingplatform.user.dto.UserRequest;
import br.edu.streamingplatform.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserRequest request) {
        UserDto user = userService.create(request);
        return ResponseEntity
                .created(URI.create("/users/" + user.id()))
                .body(user);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/{id}/exists")
    public UserExistsResponse existsById(@PathVariable Long id) {
        return new UserExistsResponse(id, userService.existsById(id));
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.update(id, request);
    }
}
