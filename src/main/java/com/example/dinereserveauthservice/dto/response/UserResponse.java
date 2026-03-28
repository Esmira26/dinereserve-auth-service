package com.example.dinereserveauthservice.dto.response;

import com.example.dinereserveauthservice.model.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {

  private UUID id;
  private String userName;
  private String phone;
  private String address;
  private UserStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
