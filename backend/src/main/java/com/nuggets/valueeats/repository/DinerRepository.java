package com.nuggets.valueeats.repository;

import com.nuggets.valueeats.entity.Diner;
import org.springframework.stereotype.Repository;

@Repository
public interface DinerRepository extends UserRepository<Diner> {
    Diner findById(int id);

    boolean existsById(Long id);

    Diner findByToken(String token);
}
