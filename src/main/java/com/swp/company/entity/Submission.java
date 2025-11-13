package com.swp.company.entity;

import com.swp.company.util.common.FormStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Submission extends AbstractEntity{

    @Enumerated(EnumType.STRING)
    private FormStatus status;

    private String response;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User employee;
    private int formVersion;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    private List<SubmissionAnswer> answers;
}
