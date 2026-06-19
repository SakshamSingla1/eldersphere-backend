package com.eldersphere.core.dto.notification_template;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateRequestBodyDTO {

    private String message;
    private String messageTo;
    private String subject;
    private String messageBody;
    private String emailTo;
    private String emailCc;
    private String emailBcc;
    private String emailReplyTo;

    @NotNull(message = "Template is required")
    private String template;
    private Integer isSms;
    private Integer isEmail;
    private Integer isWhatsapp;
    private String whatsappTemplateName;
    private String whatsappTemplateBody;
    private String additionalData;
    private String dltTemplateId;
    private Long templateGroupId;
}
