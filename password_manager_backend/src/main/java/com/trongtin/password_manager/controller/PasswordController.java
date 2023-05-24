package com.trongtin.password_manager.controller;

import com.trongtin.password_manager.model.Password;
import com.trongtin.password_manager.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passwords")
public class PasswordController {

    private final PasswordService passwordService;

    @Autowired
    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping
    public ResponseEntity<List<Password>> getAllPasswords() {
        List<Password> passwords = passwordService.findAll();
        return new ResponseEntity<>(passwords, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Password> getPasswordById(@PathVariable("id") Long id) {
        Password password = passwordService.findById(id);
        return new ResponseEntity<>(password, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Password> createPassword(@RequestBody Password password) {
        Password createdPassword = passwordService.create(password);
        return new ResponseEntity<>(createdPassword, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Password> updatePassword(@PathVariable("id") Long id, @RequestBody Password password) {
        Password updatedPassword = passwordService.update(id, password);
        return new ResponseEntity<>(updatedPassword, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassword(@PathVariable("id") Long id) {
        passwordService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}