import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Ticket, Loader2 } from 'lucide-react'
import { useAuth } from '@/store/AuthContext'
import { authService } from '@/services/authService'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { useToast } from '@/components/uicat/use-toast'

export default function LoginPage() {
  const navigate = useNavigate()
  const { signIn } = useAuth()
  const { toast } = useToast()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    try {
      const response = await authService.login(email, senha)
      signIn(response)
      navigate('/painel')
    } catch (err: any) {
      const msg = err?.response?.data?.mensagem ?? 'E-mail ou senha incorretos.'
      toast({ title: 'Erro no login', description: msg, variant: 'destructive' })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-muted/40 p-4">
      <div className="w-full max-w-sm space-y-6">
        {/* Brand */}
        <div className="flex flex-col items-center gap-2">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary">
            <Ticket className="h-7 w-7 text-primary-foreground" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight">EventPass</h1>
          <p className="text-sm text-muted-foreground">Gestão de eventos e portaria digital</p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="text-xl">Entrar</CardTitle>
            <CardDescription>Acesse seu painel de controle</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">E-mail</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="seu@email.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  autoComplete="email"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="senha">Senha</Label>
                <Input
                  id="senha"
                  type="password"
                  placeholder="••••••••"
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  required
                  autoComplete="current-password"
                />
              </div>
              <Button type="submit" className="w-full" disabled={loading}>
                {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                Entrar
              </Button>
            </form>
          </CardContent>
        </Card>

        <p className="text-center text-sm text-muted-foreground">
          Não tem conta?{' '}
          <Link to="/cadastro" className="font-medium text-primary hover:underline">
            Criar conta grátis
          </Link>
        </p>
      </div>
    </div>
  )
}
