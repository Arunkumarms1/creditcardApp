package com.bank.creditcard.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCardBillPayRequest {
    @NotBlank(message = "Credit card due amount should not be blank")
    private long totalAmountToPay;

    @NotBlank(message = "Card number should not be null")
    private String cardNumber;

}
