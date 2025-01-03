package com.bank.creditcard;

import com.bank.creditcard.entities.BankUser;
import com.bank.creditcard.entities.Role;
import com.bank.creditcard.models.CreditCardDto;
import com.bank.creditcard.models.UserDto;
import com.bank.creditcard.service.BankUserService;
import com.bank.creditcard.utils.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    JwtService jwtService;

    @MockitoBean
    BankUserService service;

    @Test
    public void testUpdateAppUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("test");
        userDto.setPassword("password");
        String token = jwtService.generateToken(getTestUser("USER"));
        when(service.loadUserByUsername(any())).thenReturn(getTestUser("USER"));
        when(service.updateUser(any())).thenReturn(userDto);
        mockMvc.perform(put("/user")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content("{\n" +
                                "    \"username\": \"testUser1\",\n" +
                                "    \"password\": \"password\",\n" +
                                "    \"email\": \"user@gmail.com\",\n" +
                                "    \"phone\" : 7907767317,\n" +
                                "    \"roles\": [\n" +
                                "       \"USER\"\n" +
                                "    ]\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"username\":\"test\",\"password\":\"password\",\"email\":null,\"phone\":null,\"createdAt\":null,\"roles\":null}"));
    }

    @Test
    public void testDeleteAppUser() throws Exception {
        String token = jwtService.generateToken(getTestUser("ADMIN"));
        when(service.loadUserByUsername(any())).thenReturn(getTestUser("ADMIN"));
        when(service.deleteUser("test")).thenReturn("User test is deleted");
        mockMvc.perform(delete("/user/test")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("User test is deleted"));
    }

    @Test
    public void testDeleteAppUserNoAuthority() throws Exception {
        String token = jwtService.generateToken(getTestUser("USER"));
        when(service.loadUserByUsername(any())).thenReturn(getTestUser("USER"));
        when(service.deleteUser("test")).thenReturn("User test is deleted");
        mockMvc.perform(delete("/user/test")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetAppUser() throws Exception {
        String token = jwtService.generateToken(getTestUser("USER"));
        UserDto userDto = new UserDto();
        userDto.setUsername("test");
        when(service.loadUserByUsername(any())).thenReturn(getTestUser("USER"));
        when(service.getUser("test")).thenReturn(userDto);
        mockMvc.perform(get("/user/test")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(content().string("{\"username\":\"test\",\"password\":null,\"email\":null,\"phone\":null,\"createdAt\":null,\"roles\":null}"));
    }

    @Test
    public void testGetAppUserCards() throws Exception {
        String token = jwtService.generateToken(getTestUser("USER"));
        UserDto userDto = new UserDto();
        userDto.setUsername("test");
        when(service.loadUserByUsername(any())).thenReturn(getTestUser("USER"));
        CreditCardDto cardDto = new CreditCardDto("4523452355",2000);
        when(service.getCards("test")).thenReturn(Collections.singleton(cardDto));
        mockMvc.perform(get("/user/cards")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(content().string("[{\"cardNumber\":\"4523452355\",\"limit\":2000}]"));
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
