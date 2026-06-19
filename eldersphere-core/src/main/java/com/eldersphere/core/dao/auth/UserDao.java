package com.eldersphere.core.dao.auth;

import com.eldersphere.core.entities.User;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.enums.UserRole;
import com.eldersphere.core.enums.UserStatus;
import com.eldersphere.core.exceptions.BadRequestException;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.ResourceNotFoundException;
import com.eldersphere.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class UserDao {

    private final UserRepository userRepository;

    public UserDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public User saveUser(User user) throws ElderSphereException {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate entry while saving user: {}", e.getMessage());
            throw BadRequestException.conflict("User already exists with the given email or phone");
        } catch (Exception e) {
            log.error("Failed to save user: {}", e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.INTERNAL_SERVER_ERROR, "Something went wrong while saving user");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public User updateUser(User user) throws ElderSphereException {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to update user id {}: {}", user.getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to update user");
        }
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone).orElse(null);
    }

    public Optional<User> findByEmailOrPhone(String email, String phone) {
        return userRepository.findByEmailOrPhone(email, phone);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public boolean existsByEmailOrPhone(String email, String phone) {
        return userRepository.existsByEmailOrPhone(email, phone);
    }

    @Transactional(rollbackOn = Exception.class)
    public void updateStatus(User user, UserStatus status) throws ElderSphereException {
        try {
            user.setStatus(status);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to update status for user id {}: {}", user.getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to update user status");
        }
    }

    public long countByRoleAndStatus(UserRole role, UserStatus status) {
        return userRepository.countByRoleAndStatus(role, status);
    }
}
