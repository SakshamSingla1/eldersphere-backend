package com.eldersphere.adminapi.service.user;

import com.eldersphere.adminapi.dto.user.request.UpdateUserStatusRequest;
import com.eldersphere.adminapi.dto.user.response.UserResponse;
import com.eldersphere.core.dao.auth.UserDao;
import com.eldersphere.core.entities.User;
import com.eldersphere.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserDao userDao;
    private final UserRepository userRepository;

    public UserResponse getUserById(Long id) {
        return toResponse(userDao.findByIdOrThrow(id));
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    public UserResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = userDao.findByIdOrThrow(id);
        userDao.updateStatus(user, request.getStatus());
        return toResponse(user);
    }

    public void deleteUser(Long id) {
        User user = userDao.findByIdOrThrow(id);
        userRepository.delete(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .regionId(user.getRegion() != null ? user.getRegion().getId() : null)
                .regionName(user.getRegion() != null ? user.getRegion().getName() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
