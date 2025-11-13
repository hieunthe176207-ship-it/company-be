package com.swp.company.entity;

import com.swp.company.util.common.UserStatus;
import jakarta.persistence.*;
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
public class User extends AbstractEntity {

    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    private String refreshToken;
    private String forgotToken;
    private String avatar;
    private boolean isBan;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private int isDeleted = 0;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ProfileHistory> profileHistory;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Document> documents;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> post;

    @OneToMany(mappedBy = "user")
    private List<Ideas> ideas;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Interview> sendInterviews;

    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL)
    private Interview interview;

    @OneToOne(mappedBy = "employee")
    private Note note;



}
