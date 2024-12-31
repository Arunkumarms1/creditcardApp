package com.bank.creditcard.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class StatementWrapper {
    private long totalAmountDue;
    private String cardNumber;
    private Set<StatementDto> statementDtoSet;
}
