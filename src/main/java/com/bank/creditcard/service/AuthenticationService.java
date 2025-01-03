package com.bank.creditcard.service;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.models.UserDto;
import com.bank.creditcard.models.AuthResponse;
import com.bank.creditcard.repositories.BankUserRepository;
import com.bank.creditcard.utils.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationService {

    private static final Logger log = LogManager.getLogger(AuthenticationService.class);
    private final AuthenticationManager authenticationManager;

    private final BankUserRepository bankUserRepository;

    private final JwtService jwtService;

    public AuthenticationService(AuthenticationManager authenticationManager, BankUserRepository bankUserRepository, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.bankUserRepository = bankUserRepository;
        this.jwtService = jwtService;
    }

    /**
     * Authentication of users, token generation
     *
     * @param user user details
     * @return Http ResponseEntity
     */
    public ResponseEntity<?> authenticateUser(UserDto user) {
        try {
            UsernamePasswordAuthenticationToken passwordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication abc = authenticationManager.authenticate(passwordAuthenticationToken);
            BankUser bankUser = bankUserRepository.findByUsername(user.getUsername()).orElseThrow();
            String token = jwtService.generateToken(bankUser);
            return ResponseEntity.ok(new AuthResponse(token, new Date(System.currentTimeMillis() + 1000 * 60 * 3)));
        } catch (AuthenticationException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
