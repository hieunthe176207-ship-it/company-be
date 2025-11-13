package com.swp.company.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SystemHistory extends AbstractEntity {
    private String content;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private User actor;
}
