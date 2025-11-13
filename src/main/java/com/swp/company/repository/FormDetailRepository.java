package com.swp.company.repository;

import com.swp.company.entity.FormDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FormDetailRepository extends CrudRepository<FormDetail, Integer> {
    void deleteByFormId(int formId);

    @Query("select d from FormDetail d where d.form.id = :id and d.version = :version")
    List<FormDetail> findAllByVersion(int id,int version);
}
