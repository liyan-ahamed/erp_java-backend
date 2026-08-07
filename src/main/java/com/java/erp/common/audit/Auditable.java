package com.java.erp.common.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that should be automatically audited.
 * Used in conjunction with AuditAspect.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    
    /**
     * The action being performed (e.g., "CREATE", "UPDATE", "DELETE", "LOGIN").
     */
    String action();
    
    /**
     * The module where the action is happening (e.g., "SCHEDULING", "USER_MANAGEMENT").
     */
    String module();
    
    /**
     * The type of entity being affected.
     */
    String entityType() default "";
}
