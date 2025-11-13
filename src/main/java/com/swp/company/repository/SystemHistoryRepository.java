package com.swp.company.repository;

import com.swp.company.entity.SystemHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SystemHistoryRepository extends JpaRepository<SystemHistory, Integer> {
    @Query("""
    SELECT s FROM SystemHistory s
    WHERE (:startDate IS NULL OR s.createdAt >= :startDate)
      AND (:endDate IS NULL OR s.createdAt <= :endDate)
""")
    Page<SystemHistory> findAllByDateAndOrder(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
