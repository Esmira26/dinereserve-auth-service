package com.example.dinereserveauthservice.service;

import com.example.dinereserveauthservice.dto.request.ForgotPasswordRequest;
import com.example.dinereserveauthservice.dto.request.LoginRequest;
import com.example.dinereserveauthservice.dto.request.RefreshRequest;
import com.example.dinereserveauthservice.dto.request.ResetPasswordRequest;
import com.example.dinereserveauthservice.dto.response.LoginResponse;
import com.example.dinereserveauthservice.dto.response.RefreshResponse;
import com.example.dinereserveauthservice.model.MyUserDetails;
import com.example.dinereserveauthservice.model.Session;
import com.example.dinereserveauthservice.model.User;
import com.example.dinereserveauthservice.repository.SessionRepository;
import com.example.dinereserveauthservice.repository.UserRepository;
import com.example.dinereserveauthservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final MyUserDetailsService myUserDetailsService;
  private final SessionRepository sessionRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final KafkaTemplate<String, Object> kafkaTemplate;

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

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("Bu email ilə istifadəçi tapılmadı!"));

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

  @Transactional
  public void forgotPassword(ForgotPasswordRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("Bu email ilə istifadəçi tapılmadı!"));

    String otp = String.format("%06d", new Random().nextInt(999999));

    user.setResetOtp(otp);
    user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
    userRepository.save(user);

    try {
      Map<String, String> event = new HashMap<>();
      event.put("email", user.getEmail());
      event.put("otp", otp);

      ObjectMapper objectMapper = new ObjectMapper();
      String jsonMessage = objectMapper.writeValueAsString(event);

      kafkaTemplate.send("password-reset-topic", jsonMessage);

    } catch (Exception e) {
      throw new RuntimeException("Kafkaya mesaj göndərilərkən xəta: " + e.getMessage());
    }

  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

    if (user.getResetOtp() == null || !user.getResetOtp().equals(request.getOtp())) {
      throw new RuntimeException("Daxil etdiyiniz kod yanlışdır!");
    }
    if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
      throw new RuntimeException("Kodun vaxtı bitib! Yenidən cəhd edin.");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));

    user.setResetOtp(null);
    user.setOtpExpiryTime(null);

    userRepository.save(user);
  }
}
