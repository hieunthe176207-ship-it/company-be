package com.swp.company.entity;

import com.swp.company.util.common.FormType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDetail extends AbstractEntity{
    private String title;
    @Enumerated(EnumType.STRING)
    private FormType type;
    private int version;
    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;
}
