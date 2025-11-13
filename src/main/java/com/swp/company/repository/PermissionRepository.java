package com.swp.company.repository;

import com.swp.company.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    List<Permission> findByPermissionIn(List<String> permissions);
}
