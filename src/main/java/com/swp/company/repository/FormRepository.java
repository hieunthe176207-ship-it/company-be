package com.swp.company.repository;

import com.swp.company.entity.Form;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FormRepository extends JpaRepository<Form, Integer> {

    @Query("select f from Form f where f.isDelete = false")
    List<Form> findAllForm(Sort sort);
}
