package com.eldersphere.core.repository;

import com.eldersphere.core.entities.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    List<UserFcmToken> findAllByUserIdAndIsActiveTrue(Long userId);

    @Modifying
    @Query("UPDATE UserFcmToken t SET t.isActive = false WHERE t.token = :token")
    void deactivateByToken(@Param("token") String token);

    boolean existsByUserIdAndToken(Long userId, String token);
}
