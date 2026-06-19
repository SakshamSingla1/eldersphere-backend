package com.eldersphere.core.service;

import com.eldersphere.core.dao.NotificationTemplateVariableDao;
import com.eldersphere.core.dto.notification_template_variable.NotificationTemplateVariableListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationTemplateVariableService {

    private final NotificationTemplateVariableDao notificationTemplateVariableDao;

    public Page<NotificationTemplateVariableListResponseDTO> getAllNotificationTemplateVariablesByCriteria(
            String search, Pageable pageable) {
        return notificationTemplateVariableDao.findNotificationTemplateVariablesByCriteria(search, pageable);
    }
}
