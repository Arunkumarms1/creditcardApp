package com.bank.creditcard;

import com.bank.creditcard.models.AuthResponse;
import com.bank.creditcard.repositories.BankUserRepository;
import com.bank.creditcard.service.AuthenticationService;
import com.bank.creditcard.service.BankUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthenticationControllerTest {

    @MockitoBean
    BankUserService userService;

    @MockitoBean
    AuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankUserRepository userRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void addUserTest() throws Exception {
        when(userService.addUser(any())).thenReturn("testUser1 Added");
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("{\n" +
                                "    \"username\": \"testUser1\",\n" +
                                "    \"password\": \"password\",\n" +
                                "    \"email\": \"user@gmail.com\",\n" +
                                "    \"phone\" : 7907767317,\n" +
                                "    \"roles\": [\n" +
                                "        \"ADMIN\",\"USER\"\n" +
                                "    ]\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(content().string("testUser1 Added"));
    }

    @Test
    public void userLoginTest() throws Exception {
        String token = "validToken";
        AuthResponse authResponse = new AuthResponse(token, dateFormat.parse("2024-12-31T11:48:22.908+00:00"));
        ResponseEntity responseEntity = ResponseEntity.ok(authResponse);
        when(authenticationService.authenticateUser(any())).thenReturn(responseEntity);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\n" +
                                "    \"username\": \"testUser1\",\n" +
                                "    \"password\": \"password\"\n}"))
                .andExpect(status().isOk());
    }

    @Test
    public void userLoginTestFail() throws Exception {
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        when(authenticationService.authenticateUser(any())).thenReturn(responseEntity);
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\n" +
                                "    \"username\": \"testUser1\",\n" +
                                "    \"password\": \"password\"\n}"))
                .andExpect(status().isUnauthorized());
    }
}
