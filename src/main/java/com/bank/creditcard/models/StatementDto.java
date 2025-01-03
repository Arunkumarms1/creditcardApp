package com.bank.creditcard.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class StatementDto {
    private Date issued;
    private long amount;
    private String merchantId;
    private boolean isPaymentCompleted;
    private PaymentType paymentType;
}
