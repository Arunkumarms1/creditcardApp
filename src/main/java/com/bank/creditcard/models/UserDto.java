package com.bank.creditcard.models;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Name is required")
    private String username;

    @Size(min=2, max=12, message = "Password must be between 2 and 12 characters long")
    private String password;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotNull
    @Min(10)
    private String phone;

    private Date createdAt;

    private Set<String> roles;
}
