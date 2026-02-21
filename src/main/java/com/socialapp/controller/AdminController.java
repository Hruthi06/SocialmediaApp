package com.socialapp.controller;

import com.socialapp.dto.ApiResponse;
import com.socialapp.entity.User;
import com.socialapp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.deleteUser(userId));
    }
    
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long postId) {
        return ResponseEntity.ok(adminService.deleteAnyPost(postId));
    }
}