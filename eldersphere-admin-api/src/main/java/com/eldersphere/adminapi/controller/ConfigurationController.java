package com.eldersphere.adminapi.controller;

import com.eldersphere.core.dto.configuration.ConfigurationListResponseDTO;
import com.eldersphere.core.dto.configuration.ConfigurationRequestBodyDTO;
import com.eldersphere.core.dto.configuration.ConfigurationResponseDTO;
import com.eldersphere.core.entities.Configuration;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.service.ConfigurationService;
import com.eldersphere.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/configuration")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping("/get-by-context")
    public ResponseEntity<ResponseModel<List<ConfigurationResponseDTO>>> getConfigurationByContext(
            @RequestParam(required = false) String contexts) throws Exception {
        return ApiResponse.respond(configurationService.getConfigurationByContext(contexts),
                "Configurations fetched successfully", "Configurations fetch failed");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<ConfigurationListResponseDTO>>> getAllConfigurationsByCriteria(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) throws Exception {
        return ApiResponse.respond(configurationService.getAllConfigurationsByCriteria(search, pageable),
                "Configurations fetched successfully", "Configurations fetch failed");
    }

    @PostMapping
    public ResponseEntity<ResponseModel<Configuration>> saveConfiguration(
            @Valid @RequestBody ConfigurationRequestBodyDTO dto) throws Exception {
        return ApiResponse.respond(configurationService.saveConfiguration(dto),
                "Configuration created successfully", "Configuration create failed");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<Configuration>> updateConfiguration(
            @PathVariable Long id,
            @Valid @RequestBody ConfigurationRequestBodyDTO dto) throws Exception {
        return ApiResponse.respond(configurationService.updateConfiguration(id, dto),
                "Configuration updated successfully", "Configuration update failed");
    }
}
