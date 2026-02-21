// Check authentication
if (!localStorage.getItem('token')) {
    window.location.href = 'login.html';
}

// Logout handler
const logoutBtn = document.getElementById('logoutBtn');
if (logoutBtn) {
    logoutBtn.addEventListener('click', (e) => {
        e.preventDefault();
        logout();
    });
}

// Load posts for regular users
async function loadFeed() {
    try {
        const result = await api.getPosts();
        const postsContainer = document.getElementById('postsContainer');
        postsContainer.innerHTML = '';
        
        result.data.forEach(post => {
            postsContainer.innerHTML += createPostCard(post);
        });
    } catch (error) {
        console.error('Error loading posts:', error);
    }
}

// Create post card HTML
function createPostCard(post) {
    const avatar = post.user.username.charAt(0).toUpperCase();
    const isLiked = post.liked || false;
    
    return `
        <div class="post-card" id="post-${post.id}">
            <div class="post-header">
                <div class="post-avatar">${avatar}</div>
                <div class="post-user-info">
                    <h4>${post.user.username}</h4>
                    <span>${new Date(post.createdAt).toLocaleDateString()}</span>
                </div>
            </div>
            <div class="post-content">
                <p>${post.content}</p>
                ${post.imageUrl ? `<img src="${post.imageUrl}" alt="Post image">` : ''}
            </div>
            <div class="post-actions">
                <button class="action-btn ${isLiked ? 'liked' : ''}" onclick="toggleLike(${post.id}, ${isLiked})">
                    ${isLiked ? '❤️' : '🤍'} Like (${post.likesCount || 0})
                </button>
                <button class="action-btn" onclick="showComments(${post.id})">
                    💬 Comment
                </button>
            </div>
            <div class="comments-section" id="comments-${post.id}">
                <div class="add-comment">
                    <input type="text" id="comment-input-${post.id}" placeholder="Write a comment...">
                    <button onclick="addComment(${post.id})">Post</button>
                </div>
            </div>
        </div>
    `;
}

// Create post
async function createPost() {
    const content = document.getElementById('postContent').value;
    const imageUrl = document.getElementById('postImage').value;
    
    if (!content.trim()) {
        alert('Please write something!');
        return;
    }
    
    try {
        await api.createPost(content, imageUrl);
        document.getElementById('postContent').value = '';
        document.getElementById('postImage').value = '';
        loadFeed();
    } catch (error) {
        alert('Error creating post: ' + error.message);
    }
}

// Toggle like
async function toggleLike(postId, isCurrentlyLiked) {
    try {
        if (isCurrentlyLiked) {
            await api.unlikePost(postId);
        } else {
            await api.likePost(postId);
        }
        loadFeed();
    } catch (error) {
        console.error('Error toggling like:', error);
    }
}

// Show comments
async function showComments(postId) {
    const commentsSection = document.getElementById(`comments-${postId}`);
    
    try {
        const result = await api.getComments(postId);
        let commentsHtml = `
            <div class="add-comment">
                <input type="text" id="comment-input-${postId}" placeholder="Write a comment...">
                <button onclick="addComment(${postId})">Post</button>
            </div>
        `;
        
        result.data.forEach(comment => {
            commentsHtml += `
                <div class="comment">
                    <div class="comment-avatar">${comment.user.username.charAt(0).toUpperCase()}</div>
                    <div class="comment-content">
                        <h5>${comment.user.username}</h5>
                        <p>${comment.content}</p>
                    </div>
                </div>
            `;
        });
        
        commentsSection.innerHTML = commentsHtml;
    } catch (error) {
        console.error('Error loading comments:', error);
    }
}

// Add comment
async function addComment(postId) {
    const input = document.getElementById(`comment-input-${postId}`);
    const content = input.value;
    
    if (!content.trim()) {
        return;
    }
    
    try {
        await api.addComment(postId, content);
        showComments(postId);
    } catch (error) {
        console.error('Error adding comment:', error);
    }
}

// Load admin dashboard
async function loadAdminDashboard() {
    try {
        // Load users
        const usersResult = await api.getUsers();
        const usersList = document.getElementById('usersList');
        usersList.innerHTML = usersResult.data.map(user => `
            <div class="admin-item">
                <div>
                    <strong>${user.username}</strong> (${user.email})
                </div>
                <button class="delete-btn" onclick="deleteUser(${user.id})">Delete</button>
            </div>
        `).join('');
        
        // Load posts
        const postsResult = await api.getPosts();
        const postsList = document.getElementById('postsList');
        postsList.innerHTML = postsResult.data.map(post => `
            <div class="admin-item">
                <div>
                    <strong>${post.user.username}</strong>: ${post.content.substring(0, 50)}...
                </div>
                <button class="delete-btn" onclick="deletePostAdmin(${post.id})">Delete</button>
            </div>
        `).join('');
        
    } catch (error) {
        console.error('Error loading admin dashboard:', error);
    }
}

// Delete user (admin)
async function deleteUser(userId) {
    if (!confirm('Are you sure you want to delete this user?')) {
        return;
    }
    
    try {
        await api.deleteUser(userId);
        loadAdminDashboard();
    } catch (error) {
        alert('Error deleting user: ' + error.message);
    }
}

// Delete post (admin)
async function deletePostAdmin(postId) {
    if (!confirm('Are you sure you want to delete this post?')) {
        return;
    }
    
    try {
        await api.deletePostAdmin(postId);
        loadAdminDashboard();
    } catch (error) {
        alert('Error deleting post: ' + error.message);
    }
}

// Initialize based on page
if (document.getElementById('postsContainer')) {
    loadFeed();
}

if (document.getElementById('usersList')) {
    loadAdminDashboard();
}