package com.bank.creditcard.controller;

import com.bank.creditcard.models.CreditCardDto;
import com.bank.creditcard.models.UserDto;
import com.bank.creditcard.exceptionhandler.InvalidInput;
import com.bank.creditcard.service.BankUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    private final BankUserService bankUserService;

    public UserController(BankUserService bankUserService) {
        this.bankUserService = bankUserService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<UserDto> updateAppUser(@RequestBody UserDto user, @AuthenticationPrincipal UserDetails userDetails) throws InvalidInput {
        UserDto updatedAppUser = bankUserService.updateUser(user,userDetails.getUsername());
        return new ResponseEntity<>(updatedAppUser, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        return ResponseEntity.ok(bankUserService.deleteUser(username));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        return ResponseEntity.ok(bankUserService.getUser(username));
    }

    @GetMapping("/cards")
    public ResponseEntity<Set<CreditCardDto>> getCreditCards(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bankUserService.getCards(userDetails.getUsername()));
    }

    //getallusers paged

}
