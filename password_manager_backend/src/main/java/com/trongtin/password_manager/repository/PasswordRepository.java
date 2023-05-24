package com.trongtin.password_manager.repository;

import com.trongtin.password_manager.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {

    // Other password-related query methods
    // ...
}
