package com.bank.creditcard.controller;

import com.bank.creditcard.exceptionhandler.InvalidInput;
import com.bank.creditcard.exceptionhandler.ResourceNotFound;
import com.bank.creditcard.models.CreditCardBillPayRequest;
import com.bank.creditcard.models.CreditCardStatementRequest;
import com.bank.creditcard.models.PaymentInfo;
import com.bank.creditcard.models.StatementWrapper;
import com.bank.creditcard.service.CoreBanking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/banking")
public class CoreBankingController {
    private static final Logger log = LogManager.getLogger(CoreBankingController.class);
    private final CoreBanking coreBankingService;

    public CoreBankingController(CoreBanking coreBankingService) {
        this.coreBankingService = coreBankingService;
    }

    @PostMapping("/pay")
    public ResponseEntity<String> makePayment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PaymentInfo paymentInfo) {
        log.error(userDetails.getUsername());
        return ResponseEntity.ok(coreBankingService.makePayment(userDetails.getUsername(), paymentInfo));
    }

    @GetMapping("/statements")
    public ResponseEntity<StatementWrapper> getStatements(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreditCardStatementRequest cardStatementDto) {
        return ResponseEntity.ok(coreBankingService.getStatements(cardStatementDto, userDetails.getUsername()));
    }

    @PostMapping("/credit/payCardBill")
    public ResponseEntity<String> payCreditCardBill(@RequestBody CreditCardBillPayRequest payRequest, @AuthenticationPrincipal UserDetails userDetails) throws InvalidInput, ResourceNotFound {
        return ResponseEntity.ok(coreBankingService.payCreditCardBill(userDetails.getUsername(), payRequest));
    }

}
