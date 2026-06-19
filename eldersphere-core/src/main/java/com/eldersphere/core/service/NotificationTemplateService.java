package com.eldersphere.core.service;

import com.eldersphere.core.context.RequestContext;
import com.eldersphere.core.dao.NotificationTemplateDao;
import com.eldersphere.core.dto.configuration.ConfigurationItemResponseDTO;
import com.eldersphere.core.dto.configuration.ConfigurationResponseDTO;
import com.eldersphere.core.dto.notification_template.NotificationTemplateListResponseDTO;
import com.eldersphere.core.dto.notification_template.NotificationTemplateRequestBodyDTO;
import com.eldersphere.core.dto.notification_template.NotificationTemplateResponseDTO;
import com.eldersphere.core.entities.NotificationTemplate;
import com.eldersphere.core.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final NotificationTemplateDao notificationTemplateDao;
    private final ModelMapper modelMapper;
    private final RequestContext requestContext;
    private final ObjectMapper objectMapper;
    private final ConfigurationService configurationService;

    @Transactional(rollbackFor = Exception.class)
    public NotificationTemplate createNotificationTemplate(NotificationTemplateRequestBodyDTO dto) {
        Long userId = requestContext.getCurrentUserId();
        NotificationTemplate entity = modelMapper.map(dto, NotificationTemplate.class);
        entity.setId(null);
        entity.setCreatedBy(userId);
        entity.setUpdatedBy(userId);
        return notificationTemplateDao.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public NotificationTemplate updateNotificationTemplate(Long id, NotificationTemplateRequestBodyDTO dto) {
        Long userId = requestContext.getCurrentUserId();
        NotificationTemplate existing = notificationTemplateDao.findById(id);
        modelMapper.map(dto, existing);
        existing.setId(id);
        existing.setUpdatedBy(userId);
        return notificationTemplateDao.save(existing);
    }

    public NotificationTemplateResponseDTO findNotificationTemplateById(Long id) throws JsonProcessingException {
        NotificationTemplate entity = notificationTemplateDao.findById(id);
        NotificationTemplateResponseDTO responseDTO = modelMapper.map(entity, NotificationTemplateResponseDTO.class);
        if (responseDTO.getTemplateGroupId() != null) {
            List<ConfigurationResponseDTO> templateGroupConfig =
                    configurationService.getConfigurationByContext(Constants.CONFIGURATIONS.TEMPLATE_GROUPS);
            if (!templateGroupConfig.isEmpty()) {
                JsonNode data = objectMapper.readTree(templateGroupConfig.get(0).getData().toString());
                List<ConfigurationItemResponseDTO> templateGroups =
                        objectMapper.readValue(data.toString(), new TypeReference<List<ConfigurationItemResponseDTO>>() {});
                templateGroups.stream()
                        .filter(item -> item.getId() != null && item.getId().equals(responseDTO.getTemplateGroupId()))
                        .findFirst()
                        .ifPresent(item -> responseDTO.setTemplateGroupName(item.getName()));
            }
        }
        return responseDTO;
    }

    public Page<NotificationTemplateListResponseDTO> getAllNotificationTemplatesByCriteria(
            String search, String templateGroupIdString, Pageable pageable) throws JsonProcessingException {
        Page<NotificationTemplateListResponseDTO> page =
                notificationTemplateDao.findNotificationTemplatesByCriteria(search, templateGroupIdString, pageable);
        if (page.isEmpty()) return page;
        List<ConfigurationResponseDTO> templateGroupConfig =
                configurationService.getConfigurationByContext(Constants.CONFIGURATIONS.TEMPLATE_GROUPS);
        if (templateGroupConfig.isEmpty()) return page;
        JsonNode data = objectMapper.readTree(templateGroupConfig.get(0).getData().toString());
        List<ConfigurationItemResponseDTO> templateGroups =
                objectMapper.readValue(data.toString(), new TypeReference<List<ConfigurationItemResponseDTO>>() {});
        Map<Long, String> templateGroupMap = templateGroups.stream()
                .filter(item -> item.getId() != null && item.getName() != null)
                .collect(Collectors.toMap(ConfigurationItemResponseDTO::getId, ConfigurationItemResponseDTO::getName));
        for (NotificationTemplateListResponseDTO dto : page.getContent()) {
            dto.setTemplateGroupName(templateGroupMap.get(dto.getTemplateGroupId()));
        }
        return page;
    }
}
