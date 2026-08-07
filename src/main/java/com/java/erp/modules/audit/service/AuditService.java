package com.java.erp.modules.audit.service;

import com.java.erp.common.response.PagedResponse;
import com.java.erp.modules.audit.dto.response.AuditLogResponse;
import com.java.erp.modules.audit.entity.AuditLog;
import com.java.erp.modules.audit.repository.AuditLogRepository;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
 * Service for recording and retrieving audit logs.
 */
@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * Record a new audit log entry.
     */
    @Transactional
    public void log(Long userId, String username, String action, String module, 
                    String entityType, Long entityId, String oldValue, String newValue, 
                    HttpServletRequest request) {
        
        AuditLog log = new AuditLog();
        
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            log.setUser(user);
        }
        
        log.setUsername(username);
        log.setAction(action);
        log.setModule(module);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        
        if (request != null) {
            log.setIpAddress(extractIpAddress(request));
            log.setUserAgent(request.getHeader("User-Agent"));
        }
        
        auditLogRepository.save(log);
        logger.debug("Audit log recorded: {} in module {}", action, module);
    }

    /**
     * Get paginated audit logs.
     * Can be extended to support complex Specification-based filtering.
     */
    @Transactional(readOnly = true)
    public PagedResponse<AuditLogResponse> getAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> logPage = auditLogRepository.findAll(pageable);
        
        List<AuditLogResponse> content = logPage.getContent().stream()
                .map(AuditLogResponse::fromEntity)
                .collect(Collectors.toList());
                
        return PagedResponse.of(content, logPage);
    }
    
    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
