package com.bank.creditcard.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class BillDto {
    @NotBlank(message = "Merchant ID should not be blank")
    private String merchantId;

    @NotBlank(message = "Bill Amount should not be blank")
    private Long amount;

    private Date issued;
}
