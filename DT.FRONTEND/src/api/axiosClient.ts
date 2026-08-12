import axios from 'axios';

// URL del Nginx Gateway (ajustar puerto si usas otro diferente a 9090)
const API_BASE_URL = 'http://localhost:9090';

export const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para inyectar el Bearer Token en cada petición
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);