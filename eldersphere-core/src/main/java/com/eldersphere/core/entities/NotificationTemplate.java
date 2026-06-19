package com.eldersphere.core.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_templates")
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "LONGTEXT")
    private String message;
    private String messageTo;
    private String subject;

    @Column(columnDefinition = "LONGTEXT")
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

    @Column(columnDefinition = "LONGTEXT")
    private String whatsappTemplateBody;
    private String additionalData;
    private String dltTemplateId;
    private Long templateGroupId;
    private Long createdBy;
    private Long updatedBy;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
