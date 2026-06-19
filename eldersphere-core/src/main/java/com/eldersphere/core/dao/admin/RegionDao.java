package com.eldersphere.core.dao.admin;

import com.eldersphere.core.entities.Region;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.ResourceNotFoundException;
import com.eldersphere.core.repository.RegionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class RegionDao {

    private final RegionRepository regionRepository;

    public RegionDao(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public Region save(Region region) throws ElderSphereException {
        try {
            return regionRepository.save(region);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate region entry: {}", e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.DUPLICATE_ENTRY, "Region already exists");
        } catch (Exception e) {
            log.error("Failed to save region: {}", e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_CREATE, "Failed to save region");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public Region update(Region region) throws ElderSphereException {
        try {
            return regionRepository.save(region);
        } catch (Exception e) {
            log.error("Failed to update region id {}: {}", region.getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to update region");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Long id) throws ElderSphereException {
        try {
            regionRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete region id {}: {}", id, e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.PROCESSING_ERROR, "Failed to delete region");
        }
    }

    public Region findById(Long id) {
        return regionRepository.findById(id).orElse(null);
    }

    public Region findByIdOrThrow(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Region", id));
    }

    public List<Region> findAll() {
        return regionRepository.findAll();
    }

    public List<Region> findAllActive() {
        return regionRepository.findAllByIsActiveTrue();
    }

    public List<Region> findAllByCountry(String country) {
        return regionRepository.findAllByCountryAndIsActiveTrue(country);
    }
}
