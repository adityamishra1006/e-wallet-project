package com.example.controller;


import com.example.dto.UserDTO;
import com.example.dto.UserProfileDTO;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public Long createUser(@RequestBody @Valid UserDTO userDTO) throws ExecutionException, InterruptedException {
        return userService.createUser(userDTO);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long id){
        UserProfileDTO userProfileDTO = userService.getUserProfile(id);
        return ResponseEntity.ok(userProfileDTO);
    }
}
