import axios, { AxiosError } from 'axios';

// Внимание: В реальном Telegram Mini App базовый URL будет вашим бекендом.
// Для целей разработки мы используем переменную окружения или дефолтный localhost.
// @ts-ignore
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://192.168.3.9:8086';

const api = axios.create({
  baseURL: API_BASE_URL,
});

// Перехватчик для добавления JWT токена
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface BackendError {
  message?: string;
  error?: string;
}

export const handleApiError = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<BackendError>;
    return axiosError.response?.data?.message || axiosError.response?.data?.error || error.message;
  }
  return String(error);
};

export default api;
