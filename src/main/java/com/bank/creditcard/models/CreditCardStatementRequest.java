package com.bank.creditcard.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CreditCardStatementRequest {
    @NotBlank(message = "Statement start date should not be blank")
    private Date startDate;

    @NotBlank(message = "Statement end date should not be blank")
    private Date endDate;

    @NotBlank(message = "Card number should not be blank")
    private String cardNumber;

    private long amount;
    private Date lastDate;

    public CreditCardStatementRequest(String cardNumber, long amount) {
        this.cardNumber = cardNumber;
        this.amount = amount;
    }

}
