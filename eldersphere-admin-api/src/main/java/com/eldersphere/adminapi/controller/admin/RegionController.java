package com.eldersphere.adminapi.controller.admin;

import com.eldersphere.adminapi.dto.region.request.RegionRequest;
import com.eldersphere.adminapi.dto.region.response.RegionResponse;
import com.eldersphere.adminapi.service.admin.RegionService;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel<RegionResponse>> createRegion(@Valid @RequestBody RegionRequest request) {
        return ApiResponse.createSuccess(regionService.createRegion(request), "Region created successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel<RegionResponse>> updateRegion(@PathVariable Long id,
                                                                      @Valid @RequestBody RegionRequest request) {
        return ApiResponse.successResponse(regionService.updateRegion(id, request), "Region updated successfully");
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel<RegionResponse>> toggleActive(@PathVariable Long id) {
        return ApiResponse.successResponse(regionService.toggleActive(id), "Region status updated");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseModel<Void>> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ApiResponse.successResponse();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<RegionResponse>> getRegionById(@PathVariable Long id) {
        return ApiResponse.successResponse(regionService.getRegionById(id));
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<RegionResponse>>> getAllRegions() {
        return ApiResponse.successResponse(regionService.getAllRegions());
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseModel<List<RegionResponse>>> getAllActiveRegions() {
        return ApiResponse.successResponse(regionService.getAllActiveRegions());
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<ResponseModel<List<RegionResponse>>> getRegionsByCountry(@PathVariable String country) {
        return ApiResponse.successResponse(regionService.getRegionsByCountry(country));
    }
}
