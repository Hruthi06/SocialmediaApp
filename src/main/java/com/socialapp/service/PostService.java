package com.socialapp.service;

import com.socialapp.dto.ApiResponse;
import com.socialapp.entity.*;
import com.socialapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private LikeRepository likeRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Transactional
    public ApiResponse<Post> createPost(Long userId, String content, String imageUrl) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        
        Post post = new Post();
        post.setUser(userOpt.get());
        post.setContent(content);
        post.setImageUrl(imageUrl);
        
        postRepository.save(post);
        return ApiResponse.success("Post created", post);
    }
    
    public ApiResponse<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        return ApiResponse.success(posts);
    }
    
    public ApiResponse<List<Post>> getUserPosts(Long userId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ApiResponse.success(posts);
    }
    
    @Transactional
    public ApiResponse<String> deletePost(Long postId, Long userId, boolean isAdmin) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ApiResponse.error("Post not found");
        }
        
        Post post = postOpt.get();
        if (!post.getUser().getId().equals(userId) && !isAdmin) {
            return ApiResponse.error("Not authorized");
        }
        
        postRepository.delete(post);
        return ApiResponse.success("Post deleted");
    }
    
    @Transactional
    public ApiResponse<String> likePost(Long userId, Long postId) {
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            return ApiResponse.error("Already liked");
        }
        
        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (postOpt.isEmpty() || userOpt.isEmpty()) {
            return ApiResponse.error("Post or user not found");
        }
        
        Like like = new Like();
        like.setUser(userOpt.get());
        like.setPost(postOpt.get());
        likeRepository.save(like);
        
        // Create notification
        Notification notification = new Notification();
        notification.setUser(postOpt.get().getUser());
        notification.setMessage(userOpt.get().getUsername() + " liked your post");
        notification.setType("LIKE");
        notificationRepository.save(notification);
        
        return ApiResponse.success("Post liked");
    }
    
    @Transactional
    public ApiResponse<String> unlikePost(Long userId, Long postId) {
        likeRepository.deleteByUserIdAndPostId(userId, postId);
        return ApiResponse.success("Post unliked");
    }
    
    @Transactional
    public ApiResponse<Comment> addComment(Long userId, Long postId, String content) {
        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (postOpt.isEmpty() || userOpt.isEmpty()) {
            return ApiResponse.error("Post or user not found");
        }
        
        Comment comment = new Comment();
        comment.setPost(postOpt.get());
        comment.setUser(userOpt.get());
        comment.setContent(content);
        commentRepository.save(comment);
        
        // Create notification
        Notification notification = new Notification();
        notification.setUser(postOpt.get().getUser());
        notification.setMessage(userOpt.get().getUsername() + " commented on your post");
        notification.setType("COMMENT");
        notificationRepository.save(notification);
        
        return ApiResponse.success("Comment added", comment);
    }
    
    public ApiResponse<List<Comment>> getComments(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        return ApiResponse.success(comments);
    }
    
    public ApiResponse<List<Notification>> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ApiResponse.success(notifications);
    }
}