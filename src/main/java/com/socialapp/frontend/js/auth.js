// Check if already logged in
if (localStorage.getItem('token') && localStorage.getItem('userRole') === 'USER') {
    window.location.href = 'feed.html';
} else if (localStorage.getItem('token') && localStorage.getItem('userRole') === 'ADMIN') {
    window.location.href = 'admin.html';
}

// Login Form Handler
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        try {
            const result = await api.login(username, password);
            localStorage.setItem('token', result.data);
            localStorage.setItem('userRole', 'USER');
            window.location.href = 'feed.html';
        } catch (error) {
            alert('Login failed: ' + error.message);
        }
    });
}

// Register Form Handler
const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const fullName = document.getElementById('fullName').value;
        const bio = document.getElementById('bio').value;
        
        try {
            const result = await api.register(username, email, password, fullName, bio);
            localStorage.setItem('token', result.data);
            localStorage.setItem('userRole', 'USER');
            window.location.href = 'feed.html';
        } catch (error) {
            alert('Registration failed: ' + error.message);
        }
    });
}

// Admin Login
const adminLoginForm = document.getElementById('adminLoginForm');
if (adminLoginForm) {
    adminLoginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        try {
            const result = await api.adminLogin(username, password);
            localStorage.setItem('token', result.data);
            localStorage.setItem('userRole', 'ADMIN');
            window.location.href = 'admin.html';
        } catch (error) {
            alert('Admin login failed: ' + error.message);
        }
    });
}

// Logout
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    window.location.href = 'login.html';
}