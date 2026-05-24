import api from '@/lib/axios'
import { AuthResponse } from '@/types'
export const authService = {
  async login(email: string, senha: string): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/login', { email, senha })
    return data
  },
  async iniciarCadastro(payload: {
    nome: string; cpf: string; email: string
    telefoneWhatsapp: string; senha: string
  }): Promise<void> {
    await api.post('/auth/cadastro/iniciar', payload)
  },
  async verificarOtp(codigo: string, telefone: string): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/verificar-otp', { codigo, telefone })
    return data
  },
}
