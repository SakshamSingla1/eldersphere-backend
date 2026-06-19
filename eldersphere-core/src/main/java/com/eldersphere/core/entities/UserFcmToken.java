package com.eldersphere.core.entities;

import com.eldersphere.core.enums.DeviceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_fcm_tokens",
    uniqueConstraints = @UniqueConstraint(name = "uq_fcm_user_token",
        columnNames = {"user_id", "token"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserFcmToken extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", length = 20)
    private DeviceType deviceType;

    @Column(name = "app_version", length = 20)
    private String appVersion;

    @Column(name = "last_active", nullable = false)
    private Instant lastActive;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
