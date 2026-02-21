package com.socialapp.service;

import com.socialapp.dto.ApiResponse;
import com.socialapp.entity.User;
import com.socialapp.entity.Post;
import com.socialapp.repository.UserRepository;
import com.socialapp.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.success(userRepository.findAll());
    }
    
    @Transactional
    public ApiResponse<String> deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            return ApiResponse.error("User not found");
        }
        userRepository.deleteById(userId);
        return ApiResponse.success("User deleted");
    }
    
    @Transactional
    public ApiResponse<String> deleteAnyPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            return ApiResponse.error("Post not found");
        }
        postRepository.deleteById(postId);
        return ApiResponse.success("Post deleted by admin");
    }
}