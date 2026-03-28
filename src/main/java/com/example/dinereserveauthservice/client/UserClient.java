package com.example.dinereserveauthservice.client;

import com.example.dinereserveauthservice.dto.request.UserRequest;
import com.example.dinereserveauthservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "dinereserve-user-service")
public interface UserClient {

  @PostMapping("/api/users")
  UserResponse createUser(UserRequest request);
}
