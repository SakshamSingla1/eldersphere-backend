package com.eldersphere.core.entities;

import com.eldersphere.core.enums.TwoFactorMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_security_settings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSecuritySettings extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "totp_secret_encrypted", columnDefinition = "TEXT")
    private String totpSecretEncrypted;

    @Column(name = "totp_enabled", nullable = false)
    @Builder.Default
    private boolean totpEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "two_factor_method", length = 20)
    private TwoFactorMethod twoFactorMethod;

    @Column(name = "last_password_change")
    private Instant lastPasswordChange;

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private Instant lockedUntil;
}
