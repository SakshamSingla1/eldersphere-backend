package com.eldersphere.adminapi.controller.user;

import com.eldersphere.adminapi.dto.address.request.AddressRequest;
import com.eldersphere.adminapi.dto.address.response.AddressResponse;
import com.eldersphere.adminapi.service.user.AddressService;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ResponseModel<AddressResponse>> createAddress(@Valid @RequestBody AddressRequest request) {
        return ApiResponse.createSuccess(addressService.createAddress(request), "Address created successfully");
    }

    @PutMapping("/{addressId}/user/{userId}")
    public ResponseEntity<ResponseModel<AddressResponse>> updateAddress(@PathVariable Long addressId,
                                                                         @PathVariable Long userId,
                                                                         @Valid @RequestBody AddressRequest request) {
        return ApiResponse.successResponse(addressService.updateAddress(addressId, userId, request), "Address updated successfully");
    }

    @DeleteMapping("/{addressId}/user/{userId}")
    public ResponseEntity<ResponseModel<Void>> deleteAddress(@PathVariable Long addressId,
                                                              @PathVariable Long userId) {
        addressService.deleteAddress(addressId, userId);
        return ApiResponse.successResponse();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<AddressResponse>> getAddressById(@PathVariable Long id) {
        return ApiResponse.successResponse(addressService.getAddressById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseModel<List<AddressResponse>>> getAddressesByUser(@PathVariable Long userId) {
        return ApiResponse.successResponse(addressService.getAddressesByUser(userId));
    }
}
