package com.example.backend.repositories;

import com.example.backend.database.models.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends IGenericRepository<User> {
    Optional<User> findByEmail(String email);
}
