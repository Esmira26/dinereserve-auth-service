package com.example.dinereserveauthservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequest {

  @Pattern(regexp = "^[A-Za-z ]+$",
      message = "UserName yalnız hərf və boşluq ola bilər")
  private String userName;

  @Pattern(regexp = "^\\+994(50|51|55|70|77|99)[0-9]{7}$",
      message = "Telefon nömrəsi +99450XXXXXXX, +99451XXXXXXX və s. formatında olmalıdır")
  private String phone;

  @Pattern(regexp = "^[\\w\\s,.-]+$",
      message = "Ünvan yalnız hərf, rəqəm və ,.- simvollarından ibarət ola bilər")
  private String address;

  @Email
  private String email;
}
