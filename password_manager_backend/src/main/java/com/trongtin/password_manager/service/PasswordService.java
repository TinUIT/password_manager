package com.trongtin.password_manager.service;

import com.trongtin.password_manager.model.Password;
import com.trongtin.password_manager.repository.PasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordService {

    private final PasswordRepository passwordRepository;

    @Autowired
    public PasswordService(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    public List<Password> findAll() {
        return passwordRepository.findAll();
    }

    public Password findById(Long id) {
        Optional<Password> passwordOptional = passwordRepository.findById(id);
        if (passwordOptional.isPresent()) {
            return passwordOptional.get();
        } else {
            throw new RuntimeException("Password not found with id: " + id);
        }
    }

    public Password create(Password password) {
        return passwordRepository.save(password);
    }

    public Password update(Long id, Password newPassword) {
        Optional<Password> passwordOptional = passwordRepository.findById(id);
        if (passwordOptional.isPresent()) {
            Password password = passwordOptional.get();
            // Update fields
            password.setServiceName(newPassword.getServiceName());
            password.setUsername(newPassword.getUsername());
            password.setEncryptedPassword(newPassword.getEncryptedPassword());
            password.setUrl(newPassword.getUrl());
            password.setNotes(newPassword.getNotes());
            password.setUpdatedAt(new Date()); // Update the 'updated_at' field
            return passwordRepository.save(password);
        } else {
            throw new RuntimeException("Password not found with id: " + id);
        }
    }

    public void delete(Long id) {
        Optional<Password> passwordOptional = passwordRepository.findById(id);
        if (passwordOptional.isPresent()) {
            passwordRepository.delete(passwordOptional.get());
        } else {
            throw new RuntimeException("Password not found with id: " + id);
        }
    }
}