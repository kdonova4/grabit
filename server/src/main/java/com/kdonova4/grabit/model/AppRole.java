package com.kdonova4.grabit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "app_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "app_role_id")
    private int appRoleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<AppUser> users;
}
