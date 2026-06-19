package com.eldersphere.core.dao;

import com.eldersphere.core.dto.notification_template_variable.NotificationTemplateVariableListResponseDTO;
import com.eldersphere.core.repository.NotificationTemplateVariableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationTemplateVariableDao {

    private final NotificationTemplateVariableRepository notificationTemplateVariableRepository;

    public Page<NotificationTemplateVariableListResponseDTO> findNotificationTemplateVariablesByCriteria(
            String search, Pageable pageable) {
        return notificationTemplateVariableRepository.findNotificationTemplateVariablesByCriteria(search, pageable);
    }
}
