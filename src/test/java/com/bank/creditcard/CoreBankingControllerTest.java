package com.bank.creditcard;

import com.bank.creditcard.controller.CoreBankingController;
import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.Role;
import com.bank.creditcard.exceptionhandler.InsufficientFunds;
import com.bank.creditcard.models.StatementWrapper;
import com.bank.creditcard.service.BankUserService;
import com.bank.creditcard.service.CoreBanking;
import com.bank.creditcard.utils.JwtService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class CoreBankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    JwtService jwtService;

    @MockitoBean
    BankUserService service;

    @MockitoBean
    CoreBanking bankingService;

    @InjectMocks
    private CoreBankingController controller;

    private final Gson gson = new Gson();

    @Test
    public void testPaySuccess() throws Exception {
        BankUser user = getTestUser();
        String token = jwtService.generateToken(user);
        when(bankingService.makePayment(any(), any())).thenReturn("Payment Completed");
        when(service.loadUserByUsername("test")).thenReturn(user);

        mockMvc.perform(post("/banking/pay")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content(paymentInfo))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment Completed"));
    }

    @Test
    public void testPayFailure() throws Exception {
        BankUser user = getTestUser();
        String token = jwtService.generateToken(user);
        when(bankingService.makePayment(any(), any())).thenThrow(new InsufficientFunds("Insufficient Funds"));
        when(service.loadUserByUsername("test")).thenReturn(user);

        mockMvc.perform(post("/banking/pay")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content(paymentInfo))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient Funds"));
    }

    @Test
    public void testGetStatementsSuccess() throws Exception {
        BankUser user = getTestUser();
        String token = jwtService.generateToken(user);
        when(bankingService.getStatements(any(), any())).thenReturn(gson.fromJson(statementsResponse, StatementWrapper.class));
        when(service.loadUserByUsername("test")).thenReturn(user);

        mockMvc.perform(get("/banking/statements")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content(statementRequest))
                .andExpect(status().isOk())
                .andExpect(content().string(statementsResponse));
    }

    @Test
    public void testGetStatementsFail() throws Exception {
        BankUser user = getTestUser();
        String token = jwtService.generateToken(user);
        when(bankingService.getStatements(any(), any())).thenThrow(new NoSuchElementException());
        when(service.loadUserByUsername("test")).thenReturn(user);

        mockMvc.perform(get("/banking/statements")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content(statementRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPayCreditCardBillSuccess() throws Exception {
        BankUser user = getTestUser();
        String token = jwtService.generateToken(user);
        when(service.loadUserByUsername("test")).thenReturn(user);
        when(bankingService.payCreditCardBill(any(),any())).thenReturn("Payment Completed");

        mockMvc.perform(post("/banking/credit/payCardBill")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content("{\n" +
                                "    \"cardNumber\": \"3301178509730880\",\n" +
                                "    \"totalAmountToPay\": \"100\"\n" +
                                "}"))
                .andExpect(content().string("Payment Completed"))
                .andExpect(status().isOk());
    }


    String paymentInfo = "{\n" +
            "    \"billDto\": {\n" +
            "        \"merchantId\": \"3306\",\n" +
            "        \"amount\": 5011111111111,\n" +
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

    private BankUser getTestUser() {
        BankUser user = new BankUser();
        user.setUsername("test");
        Role role = new Role();
        role.setName("USER");
        user.setRoles(Collections.singleton(role));
        return user;
    }
}
