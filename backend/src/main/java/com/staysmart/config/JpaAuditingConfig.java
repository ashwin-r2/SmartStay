package com.staysmart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Enables {@code @CreatedDate}/{@code @LastModifiedDate} population on {@link com.staysmart.common.entity.BaseEntity}. */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
