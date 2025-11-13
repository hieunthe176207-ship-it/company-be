package com.swp.company.repository;

import com.swp.company.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InterviewRepository extends JpaRepository<Interview, Integer> {
    @Query("Select i from Interview i where i.candidate.email = :userEmail")
    Interview findByUserEmail(String userEmail);
}
