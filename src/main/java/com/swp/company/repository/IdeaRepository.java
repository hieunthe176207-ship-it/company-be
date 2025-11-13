package com.swp.company.repository;

import com.swp.company.entity.Ideas;
import com.swp.company.util.common.FormStatus;
import com.swp.company.util.common.IdeaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IdeaRepository extends JpaRepository<Ideas, Integer> {

    @Query("select i from Ideas i where i.user.id = :id")
    Page<Ideas> findIdeasByUser(int id, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Ideas i WHERE i.status = :status")
    int countIdeas(IdeaStatus status);
}
