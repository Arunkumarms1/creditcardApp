package com.bank.creditcard.repositories;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
       Optional<CreditCard> findByCardNumber(String cardNumber);
}
