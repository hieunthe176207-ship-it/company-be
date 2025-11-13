package com.swp.company.repository;

import com.swp.company.entity.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatbotRepository extends JpaRepository<Chatbot, Integer> {
    @Query("SELECT c FROM Chatbot c JOIN c.roles r WHERE r.id = :roleId")
    List<Chatbot> findByRoleId(@Param("roleId") int roleId);
}
