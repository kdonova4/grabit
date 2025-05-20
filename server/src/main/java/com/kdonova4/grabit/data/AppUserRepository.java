package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByUsernameAndPassword(String username, String password);

    Optional<AppUser> findByUsername(String username);
}
