// API call helper with JWT token
async function apiCall(url, options = {}) {
    const token = localStorage.getItem('token');
    
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` })
        }
    };
    
    const mergedOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };
    
    return fetch(url, mergedOptions);
}

// Logout function
function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}

// Check if user is authenticated
function requireAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

// Check if user is admin
function requireAdmin() {
    const role = localStorage.getItem('role');
    if (role !== 'ROLE_ADMIN') {
        window.location.href = '/dashboard.html';
        return false;
    }
    return true;
}
