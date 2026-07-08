package com.codems.accountshield.domain.user.entity;

import com.codems.accountshield.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 150)
    private String bio;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    private boolean emailVerified;

    private boolean accountLocked;

    @Column(nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    private LocalDateTime lockedUntil;

    public boolean isAccountLocked() {
        if (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now())) {
            return true;
        }

        if (lockedUntil != null && lockedUntil.isBefore(LocalDateTime.now())) {
            this.accountLocked = false;
            this.lockedUntil = null;
            this.failedLoginAttempts = 0;
        }
        return accountLocked;
    }
}
