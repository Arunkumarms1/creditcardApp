package com.bank.creditcard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Getter
@Setter
public class CreditCard {

    public CreditCard() {
        this.cardNumber = IntStream.range(0,16).mapToObj(n -> String.valueOf(new Random().nextInt(10))).collect(Collectors.joining());
        this.issued = new Date();
        this.cvv = IntStream.range(0,3).mapToObj(n -> String.valueOf(new Random().nextInt(10))).collect(Collectors.joining());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String cardNumber;

    private String cvv;

    private long totalLimit;

    private long availableLimit;

    private long utilisedLimit;

    private long charges;

    private Date issued;

    private Date updated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BankUser user;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL)
    private Set<Statement> statements = new HashSet<>();


}
