package com.example.dinereserveauthservice.service;

import com.example.dinereserveauthservice.dto.request.LoginRequest;
import com.example.dinereserveauthservice.dto.request.RefreshRequest;
import com.example.dinereserveauthservice.dto.response.LoginResponse;
import com.example.dinereserveauthservice.dto.response.RefreshResponse;
import com.example.dinereserveauthservice.model.MyUserDetails;
import com.example.dinereserveauthservice.model.Session;
import com.example.dinereserveauthservice.model.User;
import com.example.dinereserveauthservice.repository.SessionRepository;
import com.example.dinereserveauthservice.repository.UserRepository;
import com.example.dinereserveauthservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final MyUserDetailsService myUserDetailsService;
  private final SessionRepository sessionRepository;
  private final UserRepository userRepository;

  public LoginResponse login(LoginRequest request) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

    MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

    String accessToken = jwtTokenProvider.generateToken(userDetails);
    String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

    User user = userRepository.findByEmail(request.getEmail());

    Session session = new Session();
    session.setUser(user);
    session.setRefreshToken(refreshToken);
    session.setIsDeleted(false);

    sessionRepository.save(session);

    return new LoginResponse(accessToken, refreshToken);
  }

  public RefreshResponse refreshToken(RefreshRequest request) {
    String refreshToken = request.getRefreshToken();

    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new RuntimeException("Invalid refresh token");
    }

    Session session = sessionRepository.findByRefreshTokenAndIsDeletedFalse(refreshToken)
        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

    UserDetails userDetails = myUserDetailsService.loadUserByUsername(session.getUser().getEmail());

    String newAccessToken = jwtTokenProvider.generateToken(userDetails);

     String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
     session.setRefreshToken(newRefreshToken);
     sessionRepository.save(session);

    return new RefreshResponse(newAccessToken, newRefreshToken);
  }
}
