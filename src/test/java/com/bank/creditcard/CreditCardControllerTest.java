package com.bank.creditcard;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.Role;
import com.bank.creditcard.service.BankUserService;
import com.bank.creditcard.service.CreditCardService;
import com.bank.creditcard.utils.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class CreditCardControllerTest {

    @MockitoBean
    CreditCardService creditCardService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    JwtService jwtService;

    @MockitoBean
    BankUserService service;

    @Test
    public void createCardTest() throws Exception {
        String token = jwtService.generateToken(getTestUser("USER"));
        when(service.loadUserByUsername("test")).thenReturn(getTestUser("USER"));
        when(creditCardService.addCreditCard(any(), any())).thenReturn("Card Saved");
        mockMvc.perform(post("/credit/create")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"cardNumber\": \"2523452345\", \"limit\" : \"10000\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card Saved"));
    }

    @Test
    public void createCardTestFail() throws Exception {
        String token = jwtService.generateToken(getTestUser("USER"));
        when(service.loadUserByUsername("test")).thenReturn(getTestUser("USER"));
        when(creditCardService.addCreditCard(any(), any())).thenThrow(new NoSuchElementException("no user found"));
        mockMvc.perform(post("/credit/create")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"cardNumber\": \"2523452345\", \"limit\" : \"10000\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("no user found"));
    }

    @Test
    public void updateCardTest() throws Exception {
        String token = jwtService.generateToken(getTestUser("USER"));
        when(service.loadUserByUsername("test")).thenReturn(getTestUser("USER"));
        when(creditCardService.updateCreditCard(any(), any())).thenReturn("Card Updated");
        mockMvc.perform(put("/credit/update")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"cardNumber\": \"2523452345\", \"limit\" : \"10000\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card Updated"));
    }

    @Test
    public void updateCardTestFail() throws Exception {
        String token = jwtService.generateToken(getTestUser("USER"));
        when(service.loadUserByUsername("test")).thenReturn(getTestUser("USER"));
        when(creditCardService.updateCreditCard(any(), any())).thenThrow(new NoSuchElementException("user not found"));
        mockMvc.perform(put("/credit/update")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"cardNumber\": \"2523452345\", \"limit\" : \"10000\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("user not found"));
    }

    @Test
    public void deleteCreditCard() throws Exception {
        String token = jwtService.generateToken(getTestUser("ADMIN"));
        when(service.loadUserByUsername("test")).thenReturn(getTestUser("ADMIN"));
        when(creditCardService.deleteCreditCard(any())).thenReturn("Card Deleted");
        mockMvc.perform(delete("/credit/2523452345")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Card Deleted"));
    }

    @Test
    public void deleteCreditCardFail() throws Exception {
        String token = jwtService.generateToken(getTestUser("ADMIN"));
        when(service.loadUserByUsername("test")).thenReturn(getTestUser("ADMIN"));
        when(creditCardService.deleteCreditCard(any())).thenThrow(new NoSuchElementException());
        mockMvc.perform(delete("/credit/2523452345")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCreditCardNoAuthority() throws Exception {
        String token = jwtService.generateToken(getTestUser("USER"));
        when(service.loadUserByUsername("test")).thenReturn(getTestUser("USER"));
        when(creditCardService.deleteCreditCard(any())).thenReturn("Card Deleted");
        mockMvc.perform(delete("/credit/2523452345")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private BankUser getTestUser(String roleName) {
        BankUser user = new BankUser();
        user.setUsername("test");
        Role role = new Role();
        role.setName(roleName);
        user.setRoles(Collections.singleton(role));
        return user;
    }

}
