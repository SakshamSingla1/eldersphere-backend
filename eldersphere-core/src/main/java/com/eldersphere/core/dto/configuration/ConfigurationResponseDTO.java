package com.eldersphere.core.dto.configuration;

import com.eldersphere.core.utils.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationResponseDTO {

    private Long id;
    private String context;
    private JsonNode data;

    public ConfigurationResponseDTO(Long id, String context, String data) {
        JsonHelper jsonHelper = new JsonHelper();
        this.id = id;
        this.context = context;
        this.data = jsonHelper.convertToJsonNode(data);
    }
}
