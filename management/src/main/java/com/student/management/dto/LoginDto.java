package com.student.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Enter Valid Email")
    private String email;

    @NotEmpty(message = "Password should not be empty")
    private String password;
}