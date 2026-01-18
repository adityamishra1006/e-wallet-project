package com.example.controller;

import com.example.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-service")
public class UserController {

    @PostMapping("/user")
    public Long createUser(@RequestBody @Valid UserDTO userDTO){

    }
}
