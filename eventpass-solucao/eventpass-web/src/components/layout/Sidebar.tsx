import React from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import {
  LayoutDashboard, Calendar, Smartphone, CreditCard, LogOut, Ticket,
} from 'lucide-react'
import { useAuth } from '@/store/AuthContext'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { iniciais } from '@/lib/utils'
import { cn } from '@/lib/utils'

const navItems = [
  { to: '/painel', icon: LayoutDashboard, label: 'Dashboard', end: true },
  { to: '/painel/eventos', icon: Calendar, label: 'Eventos' },
  { to: '/painel/dispositivos', icon: Smartphone, label: 'Dispositivos' },
  { to: '/painel/planos', icon: CreditCard, label: 'Planos' },
]

export default function Sidebar() {
  const { cliente, signOut } = useAuth()
  const navigate = useNavigate()

  function handleSignOut() {
    signOut()
    navigate('/login')
  }

  return (
    <aside className="flex h-screen w-60 flex-col border-r bg-card">
      {/* Logo */}
      <div className="flex items-center gap-2 px-5 py-5 border-b">
        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
          <Ticket className="h-5 w-5 text-primary-foreground" />
        </div>
        <span className="text-lg font-bold tracking-tight">EventPass</span>
      </div>

      {/* Nav */}
      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-1">
        {navItems.map(({ to, icon: Icon, label, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            className={({ isActive }) =>
              cn(
                'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-primary/10 text-primary'
                  : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
              )
            }
          >
            <Icon className="h-4 w-4 shrink-0" />
            {label}
          </NavLink>
        ))}
      </nav>

      {/* User */}
      <div className="border-t px-3 py-4">
        <div className="flex items-center gap-3 rounded-md px-2 py-2">
          <Avatar className="h-8 w-8">
            <AvatarFallback className="text-xs">
              {cliente ? iniciais(cliente.nome) : 'EP'}
            </AvatarFallback>
          </Avatar>
          <div className="flex-1 min-w-0">
            <p className="truncate text-sm font-medium">{cliente?.nome ?? '—'}</p>
            <p className="truncate text-xs text-muted-foreground">{cliente?.email ?? ''}</p>
          </div>
          <button
            onClick={handleSignOut}
            className="rounded p-1 text-muted-foreground hover:text-destructive transition-colors"
            title="Sair"
          >
            <LogOut className="h-4 w-4" />
          </button>
        </div>
      </div>
    </aside>
  )
}
