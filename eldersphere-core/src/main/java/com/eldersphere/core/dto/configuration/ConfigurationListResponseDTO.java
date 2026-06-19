package com.eldersphere.core.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationListResponseDTO {
    private Long id;
    private String context;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
