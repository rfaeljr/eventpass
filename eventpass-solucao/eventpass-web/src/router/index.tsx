import React from 'react'
import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '@/store/AuthContext'

// Public pages
import LoginPage from '@/pages/publico/LoginPage'
import CadastroPage from '@/pages/publico/CadastroPage'

// Painel pages
import MainLayout from '@/components/layout/MainLayout'
import DashboardPage from '@/pages/painel/DashboardPage'
import EventosPage from '@/pages/painel/EventosPage'
import EventoDetailPage from '@/pages/painel/EventoDetailPage'
import DispositivosPage from '@/pages/painel/DispositivosPage'
import PlanosPage from '@/pages/painel/PlanosPage'

function PrivateOutlet() {
  const { isAuthenticated } = useAuth()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return <Outlet />
}

function PublicOnlyOutlet() {
  const { isAuthenticated } = useAuth()
  if (isAuthenticated) return <Navigate to="/painel" replace />
  return <Outlet />
}

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public */}
        <Route element={<PublicOnlyOutlet />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/cadastro" element={<CadastroPage />} />
        </Route>

        {/* Protected */}
        <Route element={<PrivateOutlet />}>
          <Route element={<MainLayout />}>
            <Route path="/painel" element={<DashboardPage />} />
            <Route path="/painel/eventos" element={<EventosPage />} />
            <Route path="/painel/eventos/:id" element={<EventoDetailPage />} />
            <Route path="/painel/dispositivos" element={<DispositivosPage />} />
            <Route path="/painel/planos" element={<PlanosPage />} />
          </Route>
        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/painel" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
