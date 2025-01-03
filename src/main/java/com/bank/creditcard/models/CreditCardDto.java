package com.bank.creditcard.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardDto {

    private String cardNumber;
    @NotBlank(message = "Limit is required")
    private long limit;
    private long utilisedLimit;

    public CreditCardDto(String cardNumber, long limit) {
        this.cardNumber = cardNumber;
        this.limit = limit;
    }
}
