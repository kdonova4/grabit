package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Integer> {

    Optional<AppRole> findByRoleName(String roleName);
}
