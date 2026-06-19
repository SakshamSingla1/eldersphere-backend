package com.eldersphere.core.repository;

import com.eldersphere.core.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByUserId(Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}
