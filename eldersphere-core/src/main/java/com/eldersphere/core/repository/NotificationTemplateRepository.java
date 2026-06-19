package com.eldersphere.core.repository;

import com.eldersphere.core.dto.notification_template.NotificationTemplateListResponseDTO;
import com.eldersphere.core.entities.NotificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    @Query("SELECT new com.eldersphere.core.dto.notification_template.NotificationTemplateListResponseDTO(" +
           "nt.id, nt.subject, nt.template, nt.isSms, nt.isEmail, nt.isWhatsapp, nt.whatsappTemplateName, " +
           "nt.templateGroupId, nt.createdAt, nt.updatedAt) " +
           "FROM NotificationTemplate nt " +
           "WHERE (:search IS NULL OR LOWER(nt.template) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))) " +
           "AND (:templateGroupIds IS NULL OR nt.templateGroupId IN :templateGroupIds)")
    Page<NotificationTemplateListResponseDTO> findNotificationTemplatesByCriteria(
            @Param("search") String search,
            @Param("templateGroupIds") List<Long> templateGroupIds,
            Pageable pageable);
}
