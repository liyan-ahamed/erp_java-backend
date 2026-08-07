package com.java.erp.modules.notification.repository;

import com.java.erp.modules.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientId(Long recipientId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND (:isRead IS NULL OR n.read = :isRead)")
    Page<Notification> findByRecipientIdAndReadStatus(@Param("recipientId") Long recipientId, 
                                                      @Param("isRead") Boolean isRead, 
                                                      Pageable pageable);

    long countByRecipientIdAndReadFalse(Long recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.recipient.id = :recipientId AND n.read = false")
    void markAllAsRead(@Param("recipientId") Long recipientId);
}
