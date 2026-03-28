package com.example.dinereserveauthservice.controller;

import com.example.dinereserveauthservice.dto.request.LoginRequest;
import com.example.dinereserveauthservice.dto.request.RefreshRequest;
import com.example.dinereserveauthservice.dto.request.RegisterRequest;
import com.example.dinereserveauthservice.dto.response.LoginResponse;
import com.example.dinereserveauthservice.dto.response.RefreshResponse;
import com.example.dinereserveauthservice.dto.response.RegisterResponse;
import com.example.dinereserveauthservice.model.User;
import com.example.dinereserveauthservice.service.AuthService;
import com.example.dinereserveauthservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(userService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<RefreshResponse> refreshToken(@RequestBody RefreshRequest request) {
    try {
      RefreshResponse response = authService.refreshToken(request);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new RefreshResponse(null, null)); // lazım olsa error mesaj da əlavə edə bilərsən
    }
  }
}
