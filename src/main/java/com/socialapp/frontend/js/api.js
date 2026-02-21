const API_BASE = 'http://localhost:8080/api';

class ApiService {
    constructor() {
        this.token = localStorage.getItem('token');
    }
    
    getHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        return headers;
    }
    
    async request(method, endpoint, data = null) {
        const options = {
            method,
            headers: this.getHeaders()
        };
        
        if (data) {
            options.body = JSON.stringify(data);
        }
        
        try {
            const response = await fetch(`${API_BASE}${endpoint}`, options);
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.message || 'Request failed');
            }
            
            return result;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }
    
    // Auth endpoints
    async register(username, email, password, fullName, bio) {
        return this.request('POST', '/auth/register', { username, email, password, fullName, bio });
    }
    
    async login(username, password) {
        return this.request('POST', '/auth/login', { username, password });
    }
    
    async adminLogin(username, password) {
        return this.request('POST', '/auth/admin/login', { username, password });
    }
    
    // Post endpoints
    async getPosts() {
        return this.request('GET', '/posts');
    }
    
    async createPost(content, imageUrl = '') {
        return this.request('POST', `/posts?content=${encodeURIComponent(content)}&imageUrl=${encodeURIComponent(imageUrl)}`);
    }
    
    async deletePost(postId) {
        return this.request('DELETE', `/posts/${postId}`);
    }
    
    async likePost(postId) {
        return this.request('POST', `/posts/${postId}/like`);
    }
    
    async unlikePost(postId) {
        return this.request('DELETE', `/posts/${postId}/like`);
    }
    
    async addComment(postId, content) {
        return this.request('POST', `/posts/${postId}/comments?content=${encodeURIComponent(content)}`);
    }
    
    async getComments(postId) {
        return this.request('GET', `/posts/${postId}/comments`);
    }
    
    // Admin endpoints
    async getUsers() {
        return this.request('GET', '/admin/users');
    }
    
    async deleteUser(userId) {
        return this.request('DELETE', `/admin/users/${userId}`);
    }
    
    async deletePostAdmin(postId) {
        return this.request('DELETE', `/admin/posts/${postId}`);
    }
}

const api = new ApiService();