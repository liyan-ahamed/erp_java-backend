package com.java.erp.modules.notification.controller;

import com.java.erp.common.response.ApiResponse;
import com.java.erp.common.response.PagedResponse;
import com.java.erp.common.util.SecurityUtils;
import com.java.erp.modules.notification.dto.response.NotificationResponse;
import com.java.erp.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Endpoints for user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Returns a paginated list of notifications for the current user.")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> getUserNotifications(
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        PagedResponse<NotificationResponse> notifications = notificationService.getUserNotifications(currentUserId, isRead, page, size);
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully", notifications));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Returns the count of unread notifications for the current user.")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        long count = notificationService.getUnreadCount(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Unread count fetched", Map.of("count", count)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read.")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.markAsRead(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Marks all unread notifications as read for the current user.")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.markAllAsRead(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification", description = "Deletes a specific notification.")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.deleteNotification(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully"));
    }
}
