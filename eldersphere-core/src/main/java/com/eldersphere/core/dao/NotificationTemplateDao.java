package com.eldersphere.core.dao;

import com.eldersphere.core.dto.notification_template.NotificationTemplateListResponseDTO;
import com.eldersphere.core.entities.NotificationTemplate;
import com.eldersphere.core.exceptions.ConflictException;
import com.eldersphere.core.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NotificationTemplateDao {

    private final NotificationTemplateRepository notificationTemplateRepository;

    public NotificationTemplate save(NotificationTemplate notificationTemplate) {
        try {
            NotificationTemplate saved = notificationTemplateRepository.save(notificationTemplate);
            notificationTemplateRepository.flush();
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate entry found for Notification Template", e);
            throw new ConflictException("Notification template already exists.");
        } catch (IllegalArgumentException e) {
            log.error("Failed to save Notification template", e);
            throw new IllegalArgumentException("Invalid Notification template entity");
        } catch (Exception e) {
            log.error("Failed to save Notification template", e);
            throw new RuntimeException("Something went wrong", e);
        }
    }

    public NotificationTemplate findById(Long id) {
        return notificationTemplateRepository.findById(id).orElse(null);
    }

    public Page<NotificationTemplateListResponseDTO> findNotificationTemplatesByCriteria(
            String search, String templateGroupIdString, Pageable pageable) {
        List<Long> templateGroupIds = (templateGroupIdString != null && !templateGroupIdString.isEmpty())
                ? Arrays.stream(templateGroupIdString.split(",")).map(Long::valueOf).collect(Collectors.toList())
                : null;
        return notificationTemplateRepository.findNotificationTemplatesByCriteria(search, templateGroupIds, pageable);
    }
}
