package com.swp.company.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Permission extends AbstractEntity {
    @Column(unique = true, nullable = false)
    private String permission;
    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;
}
