package com.example.dinereserveauthservice.service;

import com.example.dinereserveauthservice.model.MyUserDetails;
import com.example.dinereserveauthservice.model.User;
import com.example.dinereserveauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Bu email ilə istifadəçi tapılmadı!"));
    if (user == null){
      throw new UsernameNotFoundException("User not found with email: " + email);
    }
    return new MyUserDetails(user);
  }
}