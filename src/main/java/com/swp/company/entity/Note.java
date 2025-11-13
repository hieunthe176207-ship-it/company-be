package com.swp.company.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Note extends AbstractEntity{
    private String title;
    private String note;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "employee_id")
    private User employee; // nhân viên được ghi chú

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
