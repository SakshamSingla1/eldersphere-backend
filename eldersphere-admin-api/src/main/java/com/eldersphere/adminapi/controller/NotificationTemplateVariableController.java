package com.eldersphere.adminapi.controller;

import com.eldersphere.core.dto.notification_template_variable.NotificationTemplateVariableListResponseDTO;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.service.NotificationTemplateVariableService;
import com.eldersphere.core.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification-template-variables")
@RequiredArgsConstructor
public class NotificationTemplateVariableController {

    private final NotificationTemplateVariableService notificationTemplateVariableService;

    @GetMapping
    public ResponseEntity<ResponseModel<Page<NotificationTemplateVariableListResponseDTO>>> getAllNotificationTemplateVariablesByCriteria(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.respond(
                notificationTemplateVariableService.getAllNotificationTemplateVariablesByCriteria(search, pageable),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }
}
