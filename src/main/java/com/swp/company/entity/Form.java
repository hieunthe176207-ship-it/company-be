package com.swp.company.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Form extends AbstractEntity {
    private String description;
    private String name;
    private int version;
    private boolean isDelete = false;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
    List<FormDetail> formDetails;
}
