package com.bank.creditcard.entities;

import com.bank.creditcard.models.PaymentType;
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

    public Statement(String merchantId, Date issued, boolean paymentStatus, PaymentType type) {
        this.merchantId = merchantId;
        this.issued = issued;
        this.paymentStatus = paymentStatus;
        this.type = type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String merchantId;

    private long amount;

    private Date issued;

    private boolean paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private CreditCard creditCard;
}
