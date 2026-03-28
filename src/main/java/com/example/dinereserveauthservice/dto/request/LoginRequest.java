package com.example.dinereserveauthservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Email format is invalid")
  private String email;

  @NotBlank(message = "Password cannot be empty")
  private String password;
}