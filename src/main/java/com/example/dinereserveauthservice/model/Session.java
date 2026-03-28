package com.example.dinereserveauthservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions")
public class Session {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      foreignKey = @ForeignKey(name = "fk_sessions_user_id")
  )
  private User user;

  @Column(name = "refresh_token", length = 255)
  private String refreshToken;

  private Boolean isDeleted;
}
