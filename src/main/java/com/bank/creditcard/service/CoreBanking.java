package com.bank.creditcard.service;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.CreditCard;
import com.bank.creditcard.entities.Statement;
import com.bank.creditcard.exceptionhandler.InsufficientFunds;
import com.bank.creditcard.exceptionhandler.InvalidInput;
import com.bank.creditcard.models.*;
import com.bank.creditcard.repositories.BankUserRepository;
import com.bank.creditcard.repositories.CreditCardRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CoreBanking {

    private static final Logger log = LogManager.getLogger(CoreBanking.class);
    private final BankUserRepository userRepository;
    private final CreditCardRepository cardRepository;

    public CoreBanking(BankUserRepository userRepository, CreditCardRepository cardRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * Make a payment
     *
     * @param username    username
     * @param paymentInfo paymentInfo containing card and bill information
     * @return String status
     */
    public String makePayment(String username, PaymentInfo paymentInfo) {
        BankUser user = userRepository.findByUsername(username).orElseThrow();
        CreditCard creditCard = cardRepository.findByCardNumber(paymentInfo.getCardDto().getCardNumber()).orElseThrow();
        if (creditCard.getAvailableLimit() >= paymentInfo.getBillDto().getAmount()) {
            creditCard.setAvailableLimit(creditCard.getAvailableLimit() - paymentInfo.getBillDto().getAmount());
            creditCard.setUtilisedLimit(creditCard.getUtilisedLimit() + paymentInfo.getBillDto().getAmount());
            Statement statements = new Statement();
            statements.setAmount(paymentInfo.getBillDto().getAmount());
            statements.setIssued(paymentInfo.getBillDto().getIssued());
            statements.setMerchantId(paymentInfo.getBillDto().getMerchantId());
            statements.setPaymentStatus(true);
            statements.setCreditCard(creditCard);
            creditCard.getStatements().add(statements);
            user.getCreditCards().add(creditCard);
            userRepository.save(user);
            return "Payment Completed";
        } else {
            throw new InsufficientFunds("Payment Failed due to insufficient funds");
        }
    }

    /**
     * Returns set of statements for credit card for a given time period
     *
     * @param statementDto Data about card number and time frame
     * @param username     username
     * @return set of statement details
     */
    public StatementWrapper getStatements(CreditCardStatementRequest statementDto, String username) {
        BankUser user = userRepository.findByUsername(username).orElseThrow();
        CreditCard creditCard = user.getCreditCards().stream()
                .filter(card -> card.getCardNumber().equals(statementDto.getCardNumber())).findAny().orElseThrow();
        Set<StatementDto> creditCardStatementDto = new HashSet<>();
        creditCard.getStatements().stream().filter(statement -> statement.getIssued().after(statementDto.getStartDate()) &&
                        statement.getIssued().before(statementDto.getEndDate()))
                .forEach(statement -> creditCardStatementDto.add(new StatementDto(statement.getIssued()
                        , statement.getAmount(), statement.getMerchantId())));
        long totalDue = creditCardStatementDto.stream().map(StatementDto::getAmount).mapToLong(Long::longValue).sum();
        return new StatementWrapper(totalDue, statementDto.getCardNumber(), creditCardStatementDto);
    }

    /**
     * pay credit card bill
     *
     * @param username   username
     * @param payRequest payRequest
     * @return String status
     */
    public String payCreditCardBill(String username, CreditCardBillPayRequest payRequest) throws InvalidInput {
        BankUser user = userRepository.findByUsername(username).orElseThrow();
        CreditCard creditCard = user.getCreditCards().stream()
                .filter(card -> card.getCardNumber().equals(payRequest.getCardNumber())).findAny().orElseThrow();
        if (payRequest.getTotalAmountToPay() <= creditCard.getUtilisedLimit()) {
            creditCard.setUtilisedLimit(creditCard.getUtilisedLimit() - payRequest.getTotalAmountToPay());
            creditCard.setAvailableLimit(creditCard.getAvailableLimit() + payRequest.getTotalAmountToPay());
            user.getCreditCards().add(creditCard);
            userRepository.save(user);
            return "Payment Completed";
        } else {
            throw new InvalidInput("Please check the payable amount");
        }
    }
}