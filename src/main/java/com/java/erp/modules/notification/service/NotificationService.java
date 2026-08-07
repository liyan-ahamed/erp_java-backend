package com.java.erp.modules.notification.service;

import com.java.erp.common.response.PagedResponse;
import com.java.erp.exception.ResourceNotFoundException;
import com.java.erp.exception.UnauthorizedException;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.UserRepository;
import com.java.erp.modules.notification.dto.response.NotificationResponse;
import com.java.erp.modules.notification.entity.Notification;
import com.java.erp.modules.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing generic system notifications.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Send a notification to a specific user.
     */
    @Transactional
    public void send(Long recipientId, Notification.NotificationType type, String title, 
                     String message, String module, Long referenceId, String referenceType, 
                     Notification.NotificationPriority priority) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", recipientId));

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setModule(module);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        
        if (priority != null) {
            notification.setPriority(priority);
        }

        notificationRepository.save(notification);
        logger.debug("Notification '{}' sent to user '{}'", title, recipientId);
    }

    /**
     * Get paginated notifications for a user, optionally filtered by read status.
     */
    @Transactional(readOnly = true)
    public PagedResponse<NotificationResponse> getUserNotifications(Long userId, Boolean isRead, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByRecipientIdAndReadStatus(userId, isRead, pageable);

        List<NotificationResponse> content = notificationPage.getContent().stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());

        return PagedResponse.of(content, notificationPage);
    }

    /**
     * Get count of unread notifications for a user.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    /**
     * Mark a specific notification as read.
     */
    @Transactional
    public void markAsRead(Long notificationId, Long currentUserId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getRecipient().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You can only access your own notifications");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    /**
     * Mark all notifications as read for a user.
     */
    @Transactional
    public void markAllAsRead(Long currentUserId) {
        notificationRepository.markAllAsRead(currentUserId);
    }

    /**
     * Delete a notification.
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long currentUserId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getRecipient().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You can only access your own notifications");
        }

        notificationRepository.delete(notification);
    }
}
