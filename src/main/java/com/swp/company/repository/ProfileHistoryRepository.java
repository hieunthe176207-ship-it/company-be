package com.swp.company.repository;

import com.swp.company.entity.ProfileHistory;
import com.swp.company.util.Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProfileHistoryRepository extends JpaRepository<ProfileHistory, Integer> {
    @Query(value = "select p from ProfileHistory p join p.user u where u.id = :userId order by p.createdAt desc")
    public List<ProfileHistory> findByUserId(@Param("userId") int userId);
}
