package com.example.dinereserveauthservice.service;

import com.example.dinereserveauthservice.client.UserClient;
import com.example.dinereserveauthservice.dto.request.RegisterRequest;
import com.example.dinereserveauthservice.dto.request.UserRequest;
import com.example.dinereserveauthservice.dto.response.RegisterResponse;
import com.example.dinereserveauthservice.model.Role;
import com.example.dinereserveauthservice.model.User;
import com.example.dinereserveauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserClient userClient;

  public RegisterResponse register(RegisterRequest request) {

    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());

    User savedUser = userRepository.save(user);

    UserRequest userRequest = new UserRequest();
    userRequest.setUserName(request.getUserName());
    userRequest.setPhone(request.getPhone());
    userRequest.setAddress(request.getAddress());

    userClient.createUser(userRequest);

    return new RegisterResponse(savedUser.getId(), "User registered successfully");
  }

  public User findByEmail(String email){
    return userRepository.findByEmail(email);
  }
}
