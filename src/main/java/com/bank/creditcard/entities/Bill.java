package com.bank.creditcard.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String merchant;

    private Date issue_date;

    private long amount;

    private boolean payment_completed;

    private Date payment_date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CreditCard credit_card;
}
