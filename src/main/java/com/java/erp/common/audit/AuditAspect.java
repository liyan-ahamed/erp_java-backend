package com.java.erp.common.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.erp.common.util.SecurityUtils;
import com.java.erp.modules.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * AOP Aspect to intercept methods annotated with @Auditable
 * and automatically record an audit log entry.
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditService auditService;
    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    public AuditAspect(AuditService auditService, HttpServletRequest request, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.request = request;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.java.erp.common.audit.Auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable auditable = method.getAnnotation(Auditable.class);

        // Capture state before execution (could be enhanced to capture old state from DB)
        Object[] args = joinPoint.getArgs();
        String oldValue = null;
        if (args != null && args.length > 0) {
            try {
                oldValue = objectMapper.writeValueAsString(args[0]);
            } catch (Exception e) {
                logger.warn("Could not serialize arguments for audit log: {}", e.getMessage());
            }
        }

        // Execute the method
        Object result = joinPoint.proceed();

        // Capture result and context after execution
        String newValue = null;
        if (result != null) {
            try {
                newValue = objectMapper.writeValueAsString(result);
            } catch (Exception e) {
                logger.warn("Could not serialize result for audit log: {}", e.getMessage());
            }
        }

        Long userId = null;
        String username = "system";
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                userId = SecurityUtils.getCurrentUserId();
                username = auth.getName();
            }
        } catch (Exception e) {
            logger.debug("Could not extract user details for audit log");
        }

        try {
            auditService.log(
                    userId,
                    username,
                    auditable.action(),
                    auditable.module(),
                    auditable.entityType(),
                    null, // Entity ID extraction would require more complex mapping
                    oldValue,
                    newValue,
                    request
            );
        } catch (Exception e) {
            logger.error("Failed to record audit log", e);
        }

        return result;
    }
}
