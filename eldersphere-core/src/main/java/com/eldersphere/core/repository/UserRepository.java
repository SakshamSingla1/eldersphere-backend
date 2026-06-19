package com.eldersphere.core.repository;

import com.eldersphere.core.entities.User;
import com.eldersphere.core.enums.UserRole;
import com.eldersphere.core.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String email, String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailOrPhone(String email, String phone);

    long countByRoleAndStatus(UserRole role, UserStatus status);
}
