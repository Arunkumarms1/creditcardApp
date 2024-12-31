package com.bank.creditcard.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String access_token;

    private Date expires_in;
}
