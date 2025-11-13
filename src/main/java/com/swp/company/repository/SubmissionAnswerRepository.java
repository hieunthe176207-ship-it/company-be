package com.swp.company.repository;

import com.swp.company.entity.SubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, Integer> {

    @Query("Select s from SubmissionAnswer s where s.submission.id =:id ")
    public List<SubmissionAnswer> findBySubmissionId(int id);
}
