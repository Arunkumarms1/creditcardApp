package com.bank.creditcard.controller;

import com.bank.creditcard.models.CreditCardDto;
import com.bank.creditcard.service.CreditCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/credit")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCreditCard(@RequestBody CreditCardDto creditCardDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(creditCardService.addCreditCard(userDetails.getUsername(), creditCardDto));
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCreditCard(@RequestBody CreditCardDto creditCardDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(creditCardService.updateCreditCard(userDetails.getUsername(), creditCardDto));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{cardNumber}")
    public ResponseEntity<String> deleteCreditCard(@PathVariable String cardNumber) {
        return ResponseEntity.ok(creditCardService.deleteCreditCard(cardNumber));
    }

}
