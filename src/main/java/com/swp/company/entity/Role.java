package com.swp.company.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends AbstractEntity {
    private String name;
    private String description;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    @ManyToMany(mappedBy = "roles")
    private List<Chatbot> chatbots;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;
}
