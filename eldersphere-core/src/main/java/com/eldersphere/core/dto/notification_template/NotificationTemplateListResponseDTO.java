package com.eldersphere.core.dto.notification_template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateListResponseDTO {
    private Long id;
    private String subject;
    private String template;
    private Integer isSms;
    private Integer isEmail;
    private Integer isWhatsapp;
    private String whatsappTemplateName;
    private Long templateGroupId;
    private String templateGroupName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public NotificationTemplateListResponseDTO(Long id, String subject, String template,
            Integer isSms, Integer isEmail, Integer isWhatsapp,
            String whatsappTemplateName, Long templateGroupId,
            Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.subject = subject;
        this.template = template;
        this.isSms = isSms;
        this.isEmail = isEmail;
        this.isWhatsapp = isWhatsapp;
        this.whatsappTemplateName = whatsappTemplateName;
        this.templateGroupId = templateGroupId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
