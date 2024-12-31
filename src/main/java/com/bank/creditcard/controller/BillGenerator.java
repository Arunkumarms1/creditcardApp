package com.bank.creditcard.controller;

import com.bank.creditcard.models.BillDto;
import com.bank.creditcard.models.CreditCardDto;
import com.bank.creditcard.models.PaymentInfo;
import com.bank.creditcard.service.CreditCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/paymentInfo")
public class BillGenerator {

    private final CreditCardService creditCardService;

    public BillGenerator(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @GetMapping({"/generate"})
    public ResponseEntity<PaymentInfo> generate(@RequestParam(defaultValue = "100") Long amount, @RequestParam String cardNumber) {
        String merchantId = IntStream.range(0,4).mapToObj(n -> String.valueOf(new Random().nextInt(10))).collect(Collectors.joining());
        BillDto bill = new BillDto(merchantId, amount, new Date());
        CreditCardDto cardDto = creditCardService.getCardByNumber(cardNumber);
        return ResponseEntity.ok(new PaymentInfo(bill, cardDto));
    }
}
