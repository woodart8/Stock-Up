package org.gentle.puma.stockup.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gentle.puma.stockup.user.enums.SignUpPath;
import org.gentle.puma.stockup.user.enums.UserRole;
import org.gentle.puma.stockup.user.enums.UserStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_info")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "sign_up_path")
    @Enumerated(EnumType.STRING)
    private SignUpPath signUpPath;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

}
