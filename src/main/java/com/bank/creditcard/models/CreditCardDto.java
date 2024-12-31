package com.bank.creditcard.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class CreditCardDto {
    @NotBlank(message = "Card number is required")
    private String cardNumber;
    @NotBlank(message = "Limit is required")
    private long limit;
}
