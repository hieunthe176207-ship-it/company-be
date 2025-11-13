package com.swp.company.repository;

import com.swp.company.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    @Query("Select n from Note n where n.employee = :userId")
    public Note findByUser(int userId );
}
