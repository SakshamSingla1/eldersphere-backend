package com.eldersphere.core.dto.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigurationRequestBodyDTO {

    @NotNull(message = "Context is required")
    private String context;

    @NotNull(message = "Data is required")
    private JsonNode data;
}
