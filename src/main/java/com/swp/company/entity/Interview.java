package com.swp.company.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Interview extends AbstractEntity {
    private LocalDateTime date;
    private String description;
    private int response;
    private String reason;
    private LocalDateTime deadline;
    @OneToOne
    @JoinColumn(name = "candidate_id")
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
}
