package com.eldersphere.adminapi.service.admin;

import com.eldersphere.adminapi.dto.region.request.RegionRequest;
import com.eldersphere.adminapi.dto.region.response.RegionResponse;
import com.eldersphere.core.dao.admin.RegionDao;
import com.eldersphere.core.entities.Region;
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
public class RegionService {

    private final RegionDao regionDao;

    public RegionResponse createRegion(RegionRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw BadRequestException.badRequest("Region name is required");
        }
        Region region = Region.builder()
                .name(request.getName())
                .state(request.getState())
                .country(request.getCountry() != null ? request.getCountry() : "India")
                .isActive(true)
                .build();
        return toResponse(regionDao.save(region));
    }

    public RegionResponse updateRegion(Long id, RegionRequest request) {
        Region region = regionDao.findByIdOrThrow(id);
        if (request.getName() != null) region.setName(request.getName());
        if (request.getState() != null) region.setState(request.getState());
        if (request.getCountry() != null) region.setCountry(request.getCountry());
        return toResponse(regionDao.update(region));
    }

    public RegionResponse toggleActive(Long id) {
        Region region = regionDao.findByIdOrThrow(id);
        region.setActive(!region.isActive());
        return toResponse(regionDao.update(region));
    }

    public void deleteRegion(Long id) {
        regionDao.findByIdOrThrow(id);
        regionDao.delete(id);
    }

    public RegionResponse getRegionById(Long id) {
        return toResponse(regionDao.findByIdOrThrow(id));
    }

    public List<RegionResponse> getAllRegions() {
        return regionDao.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RegionResponse> getAllActiveRegions() {
        return regionDao.findAllActive().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RegionResponse> getRegionsByCountry(String country) {
        return regionDao.findAllByCountry(country).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private RegionResponse toResponse(Region region) {
        return RegionResponse.builder()
                .id(region.getId())
                .name(region.getName())
                .state(region.getState())
                .country(region.getCountry())
                .isActive(region.isActive())
                .createdAt(region.getCreatedAt())
                .updatedAt(region.getUpdatedAt())
                .build();
    }
}
