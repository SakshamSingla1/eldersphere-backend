package com.eldersphere.core.repository;

import com.eldersphere.core.dto.notification_template_variable.NotificationTemplateVariableListResponseDTO;
import com.eldersphere.core.entities.NotificationTemplateVariable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTemplateVariableRepository extends JpaRepository<NotificationTemplateVariable, Long> {

    @Query("SELECT new com.eldersphere.core.dto.notification_template_variable.NotificationTemplateVariableListResponseDTO(" +
           "ntv.id, ntv.variableName, ntv.htmlContent, ntv.whatsappVariable, ntv.templateId, ntv.createdAt, ntv.updatedAt) " +
           "FROM NotificationTemplateVariable ntv " +
           "WHERE ntv.createdAt = (" +
           "    SELECT MAX(sub.createdAt) FROM NotificationTemplateVariable sub WHERE sub.variableName = ntv.variableName" +
           ") AND (:search IS NULL OR LOWER(ntv.variableName) LIKE LOWER(CONCAT('%', TRIM(:search), '%')))")
    Page<NotificationTemplateVariableListResponseDTO> findNotificationTemplateVariablesByCriteria(
            @Param("search") String search,
            Pageable pageable);
}
