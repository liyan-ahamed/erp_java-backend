package com.java.erp.config;

import com.java.erp.common.util.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * JPA configuration.
 * Enables JPA auditing to automatically populate createdBy/updatedBy fields
 * from the authenticated user's email via SecurityUtils.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityUtils.getCurrentUserEmail())
                .or(() -> Optional.of("SYSTEM"));
    }
}
