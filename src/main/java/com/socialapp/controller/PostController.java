package com.socialapp.controller;

import com.socialapp.dto.ApiResponse;
import com.socialapp.entity.Comment;
import com.socialapp.entity.Post;
import com.socialapp.entity.Notification;
import com.socialapp.service.PostService;
import com.socialapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    private Long getCurrentUserId() {
        String username = tokenProvider.getUsernameFromToken(
            org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName());
        // In real app, fetch from UserRepository
        return 1L; // Simplified
    }
    
    private boolean isAdmin() {
        try {
            String role = tokenProvider.getRoleFromToken(
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getName());
            return "ADMIN".equals(role);
        } catch (Exception e) {
            return false;
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Post>> createPost(
            @RequestParam String content,
            @RequestParam(required = false) String imageUrl) {
        return ResponseEntity.ok(postService.createPost(getCurrentUserId(), content, imageUrl));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Post>>> getUserPosts(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getUserPosts(userId));
    }
    
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.deletePost(postId, getCurrentUserId(), isAdmin()));
    }
    
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> likePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.likePost(getCurrentUserId(), postId));
    }
    
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> unlikePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.unlikePost(getCurrentUserId(), postId));
    }
    
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Comment>> addComment(
            @PathVariable Long postId,
            @RequestParam String content) {
        return ResponseEntity.ok(postService.addComment(getCurrentUserId(), postId, content));
    }
    
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<Comment>>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getComments(postId));
    }
    
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications() {
        return ResponseEntity.ok(postService.getNotifications(getCurrentUserId()));
    }
}