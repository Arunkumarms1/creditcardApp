package com.bank.creditcard.service;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.CreditCard;
import com.bank.creditcard.exceptionhandler.ResourceNotFound;
import com.bank.creditcard.models.CreditCardDto;
import com.bank.creditcard.repositories.BankUserRepository;
import com.bank.creditcard.repositories.CreditCardRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CreditCardService {

    private static final Logger log = LogManager.getLogger(CreditCardService.class);
    private final CreditCardRepository cardRepository;

    private final BankUserRepository userRepository;

    public CreditCardService(CreditCardRepository cardRepository, BankUserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a credit card to current user
     *
     * @param user          username
     * @param creditCardDto creditCard details
     * @return CreditCardDto status
     */
    public CreditCardDto addCreditCard(String user, CreditCardDto creditCardDto) {
        CreditCard creditCard = new CreditCard();
        creditCard.setTotalLimit(creditCardDto.getLimit());
        creditCard.setAvailableLimit(creditCardDto.getLimit());
        BankUser bankUser = userRepository.findByUsername(user).orElseThrow();
        creditCard.setUser(bankUser);
        CreditCard saved = cardRepository.save(creditCard);
        return new CreditCardDto(saved.getCardNumber(), saved.getTotalLimit());
    }

    /**
     * Update credit card of current user
     *
     * @param user          username
     * @param creditCardDto creditCard details
     * @return String status
     */
    public String updateCreditCard(String user, CreditCardDto creditCardDto) {
        CreditCard creditCard = cardRepository.findByCardNumber(creditCardDto.getCardNumber()).orElseThrow();
        creditCard.setTotalLimit(creditCardDto.getLimit());
        cardRepository.save(creditCard);
        return "Card Updated";
    }

    /**
     * Delete card
     *
     * @param cardNumber creditCard details
     * @return String status
     */
    public String deleteCreditCard(String cardNumber) {
        CreditCard creditCard = cardRepository.findByCardNumber(cardNumber).orElseThrow();
        if (creditCard.getAvailableLimit() == creditCard.getTotalLimit()) {
            cardRepository.delete(creditCard);
            return "Card Deleted";
        }
        return "Card Cannot be deleted. Pending dues";
    }

    /**
     * Get card details by card number
     *
     * @param cardId card number
     * @return card details
     */
    public CreditCardDto getCardByNumber(String cardId) {
        Optional<CreditCard> card = cardRepository.findByCardNumber(cardId);
        CreditCard creditCard = card.orElseThrow();
        return new CreditCardDto(creditCard.getCardNumber(), creditCard.getAvailableLimit());
    }

    /**
     * @param username
     * @param
     * @return
     */
    public CreditCardDto getCard(String username, String cardId) throws ResourceNotFound {
        Optional<CreditCard> card = cardRepository.findByCardNumber(cardId);
        CreditCard creditCard = card.orElseThrow();
        if (creditCard.getUser().getUsername().equals(username)) {
            return new CreditCardDto(creditCard.getCardNumber(), creditCard.getTotalLimit(), creditCard.getUtilisedLimit());
        }
        throw new ResourceNotFound("Card not found");
    }
}
