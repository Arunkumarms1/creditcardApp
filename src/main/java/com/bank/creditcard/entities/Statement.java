package com.bank.creditcard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String merchantId;

    private long amount;

    private Date issued;

    private boolean paymentStatus;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private CreditCard creditCard;
}
