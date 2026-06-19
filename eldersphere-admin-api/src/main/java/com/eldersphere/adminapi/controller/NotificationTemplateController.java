package com.eldersphere.adminapi.controller;

import com.eldersphere.core.dto.notification_template.NotificationTemplateListResponseDTO;
import com.eldersphere.core.dto.notification_template.NotificationTemplateRequestBodyDTO;
import com.eldersphere.core.dto.notification_template.NotificationTemplateResponseDTO;
import com.eldersphere.core.entities.NotificationTemplate;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.service.NotificationTemplateService;
import com.eldersphere.core.utils.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification-templates")
@RequiredArgsConstructor
@Slf4j
public class NotificationTemplateController {

    private final NotificationTemplateService notificationTemplateService;

    @PostMapping
    public ResponseEntity<ResponseModel<NotificationTemplate>> createNotificationTemplate(
            @RequestBody NotificationTemplateRequestBodyDTO dto) {
        return ApiResponse.respond(notificationTemplateService.createNotificationTemplate(dto),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<NotificationTemplateResponseDTO>> findNotificationTemplateById(
            @PathVariable Long id) throws JsonProcessingException {
        return ApiResponse.respond(notificationTemplateService.findNotificationTemplateById(id),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<NotificationTemplateListResponseDTO>>> getAllNotificationTemplatesByCriteria(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String templateGroupIdString,
            @PageableDefault(size = 10) Pageable pageable) throws JsonProcessingException {
        return ApiResponse.respond(
                notificationTemplateService.getAllNotificationTemplatesByCriteria(search, templateGroupIdString, pageable),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<NotificationTemplate>> updateNotificationTemplate(
            @PathVariable Long id,
            @RequestBody NotificationTemplateRequestBodyDTO dto) {
        return ApiResponse.respond(notificationTemplateService.updateNotificationTemplate(id, dto),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }
}
