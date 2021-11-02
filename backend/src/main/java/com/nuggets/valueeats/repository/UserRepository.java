package com.nuggets.valueeats.repository;

import com.nuggets.valueeats.entity.User;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface UserRepository<T extends User> extends JpaRepository<T, Long> {
    boolean existsByEmail(String email);

    T findByEmail(String email);

    boolean existsById(Long id);

    T findByToken(String token);

    boolean existsByToken(String token);

    @Query("select max(id) from User")
    Long findMaxId();
}
