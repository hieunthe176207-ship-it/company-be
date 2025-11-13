package com.swp.company.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event extends AbstractEntity {
    private String title;
    private String subTitle;
    private String description;
    private String banner;
    private LocalDate startDate;
    private LocalDate endDate;
    private int quantity;
}
