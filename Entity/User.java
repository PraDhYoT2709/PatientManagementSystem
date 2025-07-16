package com.prad.PMS.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.Email;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String username;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles", // This table will be created by Hibernate
            joinColumns = @JoinColumn(name = "user_id"),        // FK to User
            inverseJoinColumns = @JoinColumn(name = "role_id")  // FK to Role
    )
    private Set<Role> roles = new HashSet<>();
}
