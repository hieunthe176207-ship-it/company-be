package com.swp.company.repository;

import com.swp.company.entity.Submission;
import com.swp.company.util.common.FormStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    @Query("SELECT s FROM Submission s WHERE s.employee.id = :id")
    Page<Submission> findHistorySubmit(int id, Pageable pageable);

    @Query("SELECT s FROM Submission s WHERE (:id = -1 OR s.form.id = :id) " +
            "AND (:status IS NULL OR s.status = :status) " +
            "AND (:date IS NULL OR CAST(s.createdAt AS date) = CAST(:date AS date))")
    Page<Submission> findAllSubmitForm(int id, FormStatus status, String date, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.status = :status")
    int countForm(FormStatus status);

    @Query(value = """
    SELECT 
        m.month_name AS month, 
        COALESCE(COUNT(s.id), 0) AS count
    FROM (
        SELECT 1 AS month_number, 'Tháng 1' AS month_name
        UNION SELECT 2, 'Tháng 2'
        UNION SELECT 3, 'Tháng 3'
        UNION SELECT 4, 'Tháng 4'
        UNION SELECT 5, 'Tháng 5'
        UNION SELECT 6, 'Tháng 6'
        UNION SELECT 7, 'Tháng 7'
        UNION SELECT 8, 'Tháng 8'
        UNION SELECT 9, 'Tháng 9'
        UNION SELECT 10, 'Tháng 10'
        UNION SELECT 11, 'Tháng 11'
        UNION SELECT 12, 'Tháng 12'
    ) m
    LEFT JOIN submission s 
        ON MONTH(s.created_at) = m.month_number 
        AND YEAR(s.created_at) = :year
    GROUP BY m.month_number, m.month_name
    ORDER BY m.month_number
""", nativeQuery = true)
    List<Object[]> countSubmissionsByMonth(@Param("year") int year);
}
