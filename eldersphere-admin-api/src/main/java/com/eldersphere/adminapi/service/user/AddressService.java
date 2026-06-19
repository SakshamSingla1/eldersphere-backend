package com.eldersphere.adminapi.service.user;

import com.eldersphere.adminapi.dto.address.request.AddressRequest;
import com.eldersphere.adminapi.dto.address.response.AddressResponse;
import com.eldersphere.core.dao.auth.UserDao;
import com.eldersphere.core.dao.user.AddressDao;
import com.eldersphere.core.entities.Address;
import com.eldersphere.core.entities.User;
import com.eldersphere.core.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressDao addressDao;
    private final UserDao userDao;

    public AddressResponse createAddress(AddressRequest request) {
        if (request.getUserId() == null) {
            throw BadRequestException.badRequest("User ID is required");
        }
        if (request.getAddressLine1() == null || request.getAddressLine1().isBlank()) {
            throw BadRequestException.badRequest("Address line 1 is required");
        }
        if (request.getCity() == null || request.getCity().isBlank()) {
            throw BadRequestException.badRequest("City is required");
        }
        User user = userDao.findByIdOrThrow(request.getUserId());
        Address address = Address.builder()
                .user(user)
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry() != null ? request.getCountry() : "India")
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        return toResponse(addressDao.save(address));
    }

    public AddressResponse updateAddress(Long addressId, Long userId, AddressRequest request) {
        Address address = addressDao.findByIdOrThrow(addressId);
        if (request.getAddressLine1() != null) address.setAddressLine1(request.getAddressLine1());
        if (request.getAddressLine2() != null) address.setAddressLine2(request.getAddressLine2());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getState() != null) address.setState(request.getState());
        if (request.getPincode() != null) address.setPincode(request.getPincode());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getLatitude() != null) address.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) address.setLongitude(request.getLongitude());
        return toResponse(addressDao.update(address));
    }

    public void deleteAddress(Long addressId, Long userId) {
        addressDao.deleteByIdAndUserId(addressId, userId);
    }

    public AddressResponse getAddressById(Long id) {
        return toResponse(addressDao.findByIdOrThrow(id));
    }

    public List<AddressResponse> getAddressesByUser(Long userId) {
        return addressDao.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
