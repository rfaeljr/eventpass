import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react'
import { AuthResponse, Cliente } from '@/types'

interface AuthState {
  token: string | null
  cliente: Cliente | null
  emTrial: boolean
}

interface AuthContextValue extends AuthState {
  signIn: (response: AuthResponse) => void
  signOut: () => void
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextValue | null>(null)

const STORAGE_TOKEN = 'eventpass_token'
const STORAGE_CLIENTE = 'eventpass_cliente'

function loadInitialState(): AuthState {
  try {
    const token = localStorage.getItem(STORAGE_TOKEN)
    const raw = localStorage.getItem(STORAGE_CLIENTE)
    const cliente: Cliente | null = raw ? JSON.parse(raw) : null
    return { token, cliente, emTrial: false }
  } catch {
    return { token: null, cliente: null, emTrial: false }
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AuthState>(loadInitialState)

  const signIn = useCallback((response: AuthResponse) => {
    localStorage.setItem(STORAGE_TOKEN, response.token)
    const cliente: Cliente = {
      id: 0,
      nome: response.nome,
      email: response.email,
      telefoneWhatsapp: '',
      status: 'ATIVO',
    }
    localStorage.setItem(STORAGE_CLIENTE, JSON.stringify(cliente))
    setState({ token: response.token, cliente, emTrial: response.emTrial })
  }, [])

  const signOut = useCallback(() => {
    localStorage.removeItem(STORAGE_TOKEN)
    localStorage.removeItem(STORAGE_CLIENTE)
    setState({ token: null, cliente: null, emTrial: false })
  }, [])

  return (
    <AuthContext.Provider
      value={{ ...state, isAuthenticated: !!state.token, signIn, signOut }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth deve ser usado dentro de AuthProvider')
  return ctx
}
