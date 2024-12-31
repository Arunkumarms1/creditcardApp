package com.bank.creditcard;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.CreditCard;
import com.bank.creditcard.entities.Role;
import com.bank.creditcard.entities.Statement;
import com.bank.creditcard.models.CreditCardStatementRequest;
import com.bank.creditcard.models.PaymentInfo;
import com.bank.creditcard.models.StatementWrapper;
import com.bank.creditcard.repositories.BankUserRepository;
import com.bank.creditcard.repositories.CreditCardRepository;
import com.bank.creditcard.service.CoreBanking;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

@SpringBootTest
public class CoreBankingServiceTest {
    @Autowired
    CoreBanking bankingService;

    @MockitoBean
    private BankUserRepository userRepository;

    @MockitoBean
    private CreditCardRepository cardRepository;

    private final Gson gson = new Gson();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void testMakePayment() throws ParseException {
        CreditCard creditCard = getCreditCard();
        BankUser user = getTestUser();
        user.setCreditCards(new HashSet<>());
        creditCard.setStatements(new HashSet<>());
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findByCardNumber(any())).thenReturn(Optional.of(creditCard));
        String result = bankingService.makePayment("test", gson.fromJson(paymentInfo, PaymentInfo.class));
        assertEquals("Payment Completed", result);
    }

    @Test
    public void testMakePaymentInsufficientFunds() throws ParseException {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(getTestUser()));
        when(cardRepository.findByCardNumber(any())).thenReturn(Optional.of(getCreditCard()));
        PaymentInfo paymentInfoObj = gson.fromJson(paymentInfo, PaymentInfo.class);
        paymentInfoObj.getBillDto().setAmount(10000L);
        try {
            String result = bankingService.makePayment("test", paymentInfoObj);
        } catch (Exception e) {
            assertEquals("Payment Failed due to insufficient funds", e.getMessage());
        }
    }

    @Test
    public void testGetStatements() throws ParseException {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(getTestUser()));
        StatementWrapper result = bankingService.getStatements(gson.fromJson(statementRequest, CreditCardStatementRequest.class), "test");
        assertEquals(1, result.getStatementDtoSet().size());
    }

    String paymentInfo = "{\n" +
            "    \"billDto\": {\n" +
            "        \"merchantId\": \"3306\",\n" +
            "        \"amount\": 50,\n" +
            "        \"issued\": \"2024-12-30T09:00:27.666+00:00\"\n" +
            "    },\n" +
            "    \"cardDto\": {\n" +
            "        \"cardNumber\": \"3301178509730880\",\n" +
            "        \"limit\": 16950\n" +
            "    }\n" +
            "}";

    String statementsResponse = "{\"totalAmountDue\":200,\"cardNumber\":\"3301178509730880\",\"statementDtoSet\":[{\"issued\":\"2024-12-30T09:00:27.666+00:00\",\"amount\":50,\"merchantId\":\"3306\"}]}";

    String statementRequest = "{\n" +
            "    \n" +
            "        \"cardNumber\": \"3301178509730880\",\n" +
            "        \"startDate\": \"2024-12-29\",\n" +
            "        \"endDate\" : \"2024-12-31\"\n" +
            "}";

    private BankUser getTestUser() throws ParseException {
        BankUser user = new BankUser();
        user.setUsername("test");
        Role role = new Role();
        role.setName("USER");
        user.setRoles(Collections.singleton(role));
        user.setCreditCards(Collections.singleton(getCreditCard()));
        return user;
    }

    private CreditCard getCreditCard() throws ParseException {
        CreditCard creditCard = new CreditCard();
        creditCard.setAvailableLimit(2000);
        creditCard.setUtilisedLimit(0);
        creditCard.setCardNumber("3301178509730880");
        creditCard.setTotalLimit(2000);
        creditCard.setIssued(new Date());
        StatementWrapper wrapper = gson.fromJson(statementsResponse, StatementWrapper.class);
        Statement statement = new Statement();
        wrapper.getStatementDtoSet().forEach(statementDto -> statement.setAmount(statementDto.getAmount()));
        statement.setIssued(dateFormat.parse("2024-12-30"));
        creditCard.setStatements(Collections.singleton(statement));
        return creditCard;

    }
}
