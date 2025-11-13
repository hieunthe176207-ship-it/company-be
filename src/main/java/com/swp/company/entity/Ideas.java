package com.swp.company.entity;
import com.swp.company.util.common.IdeaStatus;
import jakarta.persistence.*;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ideas extends AbstractEntity {

    @Column(nullable = false)
    private String content;

    private String reply;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdeaStatus status = IdeaStatus.CHUA_PHAN_HOI;
    private boolean isAnonymous;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
