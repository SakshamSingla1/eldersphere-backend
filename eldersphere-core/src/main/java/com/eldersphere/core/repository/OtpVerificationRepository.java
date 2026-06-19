package com.eldersphere.core.repository;

import com.eldersphere.core.entities.OtpVerification;
import com.eldersphere.core.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByIdentifierAndPurposeAndUsedFalseAndExpiresAtAfter(
            String identifier, OtpPurpose purpose, Instant now);

    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.identifier = :identifier AND o.purpose = :purpose")
    void deleteByIdentifierAndPurpose(@Param("identifier") String identifier,
                                      @Param("purpose") OtpPurpose purpose);

    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.expiresAt < :now")
    void deleteAllExpired(@Param("now") Instant now);
}
