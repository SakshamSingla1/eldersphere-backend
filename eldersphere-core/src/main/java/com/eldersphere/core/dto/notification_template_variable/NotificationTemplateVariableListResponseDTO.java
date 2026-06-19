package com.eldersphere.core.dto.notification_template_variable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateVariableListResponseDTO {
    private Long id;
    private String variableName;
    private String htmlContent;
    private Long whatsappVariable;
    private Long templateId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
