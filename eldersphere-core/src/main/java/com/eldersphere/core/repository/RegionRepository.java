package com.eldersphere.core.repository;

import com.eldersphere.core.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    List<Region> findAllByIsActiveTrue();

    List<Region> findAllByCountryAndIsActiveTrue(String country);
}
