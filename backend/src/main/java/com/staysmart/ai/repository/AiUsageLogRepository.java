package com.staysmart.ai.repository;

import com.staysmart.ai.entity.AiFeature;
import com.staysmart.ai.entity.AiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {

    long countByFeature(AiFeature feature);

    long countByFeatureAndSuccess(AiFeature feature, boolean success);

    long countBySuccess(boolean success);

    @Query("select coalesce(avg(l.latencyMs), 0) from AiUsageLog l where l.feature = :feature and l.success = true")
    Double averageLatencyMs(@Param("feature") AiFeature feature);

    @Query("select l.feature, count(l) from AiUsageLog l group by l.feature")
    List<Object[]> countGroupedByFeature();

    long countByCreatedAtAfter(Instant since);
}
