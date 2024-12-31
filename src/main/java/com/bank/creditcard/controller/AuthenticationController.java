package com.bank.creditcard.controller;

import com.bank.creditcard.models.AuthResponse;
import com.bank.creditcard.models.UserDto;
import com.bank.creditcard.exceptionhandler.InvalidInput;
import com.bank.creditcard.service.AuthenticationService;
import com.bank.creditcard.service.BankUserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private static final Logger log = LogManager.getLogger(AuthenticationController.class);
    private final BankUserService userService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(BankUserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@Valid @RequestBody UserDto user) throws InvalidInput {
        return ResponseEntity.ok(userService.addUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto user) {
        return authenticationService.authenticateUser(user);
    }

}
