package com.eldersphere.core.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationItemResponseDTO {
    private Long id;
    private String label;
    private String name;
    private String key;
}
