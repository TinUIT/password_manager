package com.trongtin.password_manager.service;

import com.trongtin.password_manager.dto.UserDto;
import com.trongtin.password_manager.model.User;
import com.trongtin.password_manager.util.JwtUtils;
import com.trongtin.password_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,  JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public User registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()) != null ||
                userRepository.findByEmail(userDto.getEmail()) != null) {
            return null;
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        user.setTwoFactorAuthEnabled(false);

        return userRepository.save(user);
    }

    public String loginUser(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername());

        if (user != null && passwordEncoder.matches(userDto.getPassword(), user.getPasswordHash())) {
            return jwtUtils.generateToken(user.getUsername());
        }

        return null;
    }
}