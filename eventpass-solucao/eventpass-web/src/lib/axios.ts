import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
})

// Injeta o token JWT em todas as requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('eventpass_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Redireciona para login se 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('eventpass_token')
      localStorage.removeItem('eventpass_cliente')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api