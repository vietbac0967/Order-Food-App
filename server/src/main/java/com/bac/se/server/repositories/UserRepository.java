package com.bac.se.server.repositories;

import com.bac.se.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByPhone(String phone);
}
