package com.socialapp.service;

import com.socialapp.dto.*;
import com.socialapp.entity.User;
import com.socialapp.entity.Admin;
import com.socialapp.repository.UserRepository;
import com.socialapp.repository.AdminRepository;
import com.socialapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Email already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setBio(request.getBio());
        
        userRepository.save(user);
        
        String token = tokenProvider.generateToken(user.getUsername(), "USER");
        return ApiResponse.success("Registration successful", token);
    }
    
    public ApiResponse<String> login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        
        if (userOpt.isEmpty()) {
            return ApiResponse.error("Invalid credentials");
        }
        
        User user = userOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.error("Invalid credentials");
        }
        
        String token = tokenProvider.generateToken(user.getUsername(), "USER");
        return ApiResponse.success("Login successful", token);
    }
    
    public ApiResponse<String> adminLogin(LoginRequest request) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(request.getUsername());
        
        if (adminOpt.isEmpty()) {
            return ApiResponse.error("Invalid admin credentials");
        }
        
        Admin admin = adminOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return ApiResponse.error("Invalid admin credentials");
        }
        
        String token = tokenProvider.generateToken(admin.getUsername(), "ADMIN");
        return ApiResponse.success("Admin login successful", token);
    }
}