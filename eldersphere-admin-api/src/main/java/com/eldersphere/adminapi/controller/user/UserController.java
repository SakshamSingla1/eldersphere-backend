package com.eldersphere.adminapi.controller.user;

import com.eldersphere.adminapi.dto.user.request.UpdateUserStatusRequest;
import com.eldersphere.adminapi.dto.user.response.UserResponse;
import com.eldersphere.adminapi.service.user.UserService;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<UserResponse>> getUserById(@PathVariable Long id) {
        return ApiResponse.successResponse(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.successResponse(userService.getAllUsers(pageable));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseModel<UserResponse>> updateUserStatus(@PathVariable Long id,
                                                                         @Valid @RequestBody UpdateUserStatusRequest request) {
        return ApiResponse.successResponse(userService.updateUserStatus(id, request), "User status updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseModel<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.successResponse();
    }
}
