package com.eldersphere.core.entities;

import com.eldersphere.core.enums.OtpPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "otp_verifications",
    uniqueConstraints = @UniqueConstraint(name = "uq_otp_identifier_purpose",
        columnNames = {"identifier", "purpose"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpVerification extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String identifier;

    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OtpPurpose purpose;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;
}
