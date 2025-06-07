package com.kdonova4.grabit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_id")
    private int appUserId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;


    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 2048)
    private String password;

    @Column(nullable = false)
    private boolean disabled;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "app_user_role",
        joinColumns = @JoinColumn(name = "app_user_id"),
        inverseJoinColumns = @JoinColumn(name = "app_role_id"))
    private Set<AppRole> roles;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return appUserId == appUser.appUserId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appUserId);
    }

    public AppUser(AppUser source) {
        this.appUserId = source.getAppUserId();
        this.username = source.getUsername();
        this.email = source.getEmail();
        this.password = source.getPassword();
        this.disabled = source.isDisabled();
        this.roles = source.getRoles();
    }
}
