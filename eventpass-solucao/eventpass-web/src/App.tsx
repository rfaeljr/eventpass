import React from 'react'
import { AuthProvider } from '@/store/AuthContext'
import AppRouter from '@/router'
import { Toaster } from '@/components/uicat/toaster'

export default function App() {
  return (
    <AuthProvider>
      <AppRouter />
      <Toaster />
    </AuthProvider>
  )
}
