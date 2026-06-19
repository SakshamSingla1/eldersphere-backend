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
public class NotificationTemplateResponseDTO {
    private Long id;
    private String message;
    private String messageTo;
    private String subject;
    private String messageBody;
    private String emailTo;
    private String emailCc;
    private String emailBcc;
    private String emailReplyTo;
    private String template;
    private Integer isSms;
    private Integer isEmail;
    private Integer isWhatsapp;
    private String whatsappTemplateName;
    private String whatsappTemplateBody;
    private String additionalData;
    private String dltTemplateId;
    private Long templateGroupId;
    private String templateGroupName;
    private Long createdBy;
    private Long updatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
