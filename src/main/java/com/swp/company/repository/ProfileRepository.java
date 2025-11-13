package com.swp.company.repository;

import com.swp.company.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Profile findByPhone(String phone);

} 