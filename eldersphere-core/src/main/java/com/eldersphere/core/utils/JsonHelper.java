package com.eldersphere.core.utils;

import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.exceptions.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class JsonHelper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode convertToJsonNode(String data) {
        try {
            if (data != null) {
                return this.objectMapper.readTree(data);
            } else {
                return new ObjectMapper().createArrayNode();
            }
        } catch (JsonProcessingException e) {
            log.error("Error converting string to JsonNode");
            return new ObjectMapper().createArrayNode();
        }
    }

    public JsonNode filterDuplicates(JsonNode dataNode) {
        if (!dataNode.isArray()) {
            throw BadRequestException.badRequest("Data must be an array");
        }
        Set<String> uniqueElementSet = new HashSet<>();
        ArrayNode uniqueArray = new ObjectMapper().createArrayNode();
        for (JsonNode node : dataNode) {
            String nodeStr = node.toString();
            if (uniqueElementSet.add(nodeStr)) {
                uniqueArray.add(node);
            }
        }
        return uniqueArray;
    }
}
