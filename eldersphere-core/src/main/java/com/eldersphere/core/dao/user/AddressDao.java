package com.eldersphere.core.dao.user;

import com.eldersphere.core.entities.Address;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.ResourceNotFoundException;
import com.eldersphere.core.repository.AddressRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class AddressDao {

    private final AddressRepository addressRepository;

    public AddressDao(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public Address save(Address address) throws ElderSphereException {
        try {
            return addressRepository.save(address);
        } catch (Exception e) {
            log.error("Failed to save address for user id {}: {}", address.getUser().getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_CREATE, "Failed to save address");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public Address update(Address address) throws ElderSphereException {
        try {
            return addressRepository.save(address);
        } catch (Exception e) {
            log.error("Failed to update address id {}: {}", address.getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to update address");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteByIdAndUserId(Long addressId, Long userId) throws ElderSphereException {
        try {
            addressRepository.deleteByIdAndUserId(addressId, userId);
        } catch (Exception e) {
            log.error("Failed to delete address id {} for user id {}: {}", addressId, userId, e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.PROCESSING_ERROR, "Failed to delete address");
        }
    }

    public Address findById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    public Address findByIdOrThrow(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Address", id));
    }

    public List<Address> findAllByUserId(Long userId) {
        return addressRepository.findAllByUserId(userId);
    }
}
