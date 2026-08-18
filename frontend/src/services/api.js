import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add a request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && token !== 'undefined' && token !== 'null') {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Add a response interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      
      if (status === 401) {
        // Handle unauthorized access (e.g., token expired)
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      } else if (status === 403) {
        // Handle forbidden access
        console.error("403 Forbidden: You don't have access to this resource.");
      }

      // Format custom error object
      return Promise.reject({
        message: data?.message || 'Đã có lỗi xảy ra',
        errorCode: data?.errorCode || 'UNCATEGORIZED_EXCEPTION',
        fieldErrors: data?.data || {},
        status: status
      });
    }
    
    return Promise.reject({
      message: error.message || 'Lỗi kết nối máy chủ',
      errorCode: 'NETWORK_ERROR',
      fieldErrors: {},
      status: 0
    });
  }
);

export default api;
