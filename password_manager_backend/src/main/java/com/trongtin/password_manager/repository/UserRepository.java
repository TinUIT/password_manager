package com.trongtin.password_manager.repository;

import com.trongtin.password_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query methods

    User findByUsername(String username);

    User findByEmail(String email);
}
