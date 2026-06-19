package com.eldersphere.core.repository;

import com.eldersphere.core.entities.UserSecuritySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSecuritySettingsRepository extends JpaRepository<UserSecuritySettings, Long> {

    Optional<UserSecuritySettings> findByUserId(Long userId);
}
