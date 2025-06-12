package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByUsernameAndPassword(String username, String password);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String Email);

    Optional<AppUser> findByUsernameAndEmailAndPassword(String username, String email, String password);

    @Query("SELECT u FROM AppUser u JOIN FETCH u.roles WHERE u.id = :id")
    Optional<AppUser> findByIdWithRoles(@Param("id") Integer id);


}
