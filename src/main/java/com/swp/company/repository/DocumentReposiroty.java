package com.swp.company.repository;

import com.swp.company.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DocumentReposiroty extends JpaRepository<Document, Integer> {
    @Query("""
    SELECT d FROM Document d 
    WHERE d.user.id = :userId 
      AND d.type = 'CV'
    ORDER BY d.createdAt DESC
""")
    Document findTopCVByUserId(@Param("userId") int userId);

    @Transactional
    @Modifying
    @Query("delete from Document d where d.user.id = :id and d.type = :type ")
    void deleteDocument(@Param("id") int id, @Param("type") String type);
}
