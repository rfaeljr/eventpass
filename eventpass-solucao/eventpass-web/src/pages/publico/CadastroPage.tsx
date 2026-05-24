import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Ticket, Loader2, CheckCircle2 } from 'lucide-react'
import { useAuth } from '@/store/AuthContext'
import { authService } from '@/services/authService'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { useToast } from '@/components/uicat/use-toast'

type Step = 'form' | 'otp'

interface FormData {
  nome: string
  cpf: string
  email: string
  telefoneWhatsapp: string
  senha: string
  confirmarSenha: string
}

export default function CadastroPage() {
  const navigate = useNavigate()
  const { signIn } = useAuth()
  const { toast } = useToast()
  const [step, setStep] = useState<Step>('form')
  const [loading, setLoading] = useState(false)
  const [otp, setOtp] = useState('')
  const [form, setForm] = useState<FormData>({
    nome: '', cpf: '', email: '', telefoneWhatsapp: '', senha: '', confirmarSenha: '',
  })

  function set(field: keyof FormData) {
    return (e: React.ChangeEvent<HTMLInputElement>) =>
      setForm((prev) => ({ ...prev, [field]: e.target.value }))
  }

  async function handleCadastro(e: React.FormEvent) {
    e.preventDefault()
    if (form.senha !== form.confirmarSenha) {
      toast({ title: 'Senhas diferentes', description: 'As senhas não coincidem.', variant: 'destructive' })
      return
    }
    setLoading(true)
    try {
      await authService.iniciarCadastro({
        nome: form.nome,
        cpf: form.cpf.replace(/\D/g, ''),
        email: form.email,
        telefoneWhatsapp: form.telefoneWhatsapp.replace(/\D/g, ''),
        senha: form.senha,
      })
      toast({ title: 'Código enviado!', description: 'Verifique seu WhatsApp para o código de 6 dígitos.' })
      setStep('otp')
    } catch (err: any) {
      const msg = err?.response?.data?.mensagem ?? 'Erro ao criar conta. Tente novamente.'
      toast({ title: 'Erro no cadastro', description: msg, variant: 'destructive' })
    } finally {
      setLoading(false)
    }
  }

  async function handleOtp(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    try {
      const response = await authService.verificarOtp(otp, form.telefoneWhatsapp.replace(/\D/g, ''))
      signIn(response)
      toast({ title: 'Conta verificada! 🎉', description: `Bem-vindo, ${response.nome}! Trial de 14 dias ativado.` })
      navigate('/painel')
    } catch (err: any) {
      const msg = err?.response?.data?.mensagem ?? 'Código inválido ou expirado.'
      toast({ title: 'Erro na verificação', description: msg, variant: 'destructive' })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-muted/40 p-4">
      <div className="w-full max-w-sm space-y-6">
        <div className="flex flex-col items-center gap-2">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary">
            <Ticket className="h-7 w-7 text-primary-foreground" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight">EventPass</h1>
          <p className="text-sm text-muted-foreground">14 dias grátis · Sem cartão necessário</p>
        </div>

        {step === 'form' ? (
          <Card>
            <CardHeader>
              <CardTitle className="text-xl">Criar conta</CardTitle>
              <CardDescription>Preencha seus dados para começar</CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleCadastro} className="space-y-3">
                <div className="space-y-1.5">
                  <Label htmlFor="nome">Nome completo</Label>
                  <Input id="nome" placeholder="João da Silva" value={form.nome} onChange={set('nome')} required />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="cpf">CPF</Label>
                  <Input id="cpf" placeholder="000.000.000-00" value={form.cpf} onChange={set('cpf')} required maxLength={14} />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="email">E-mail</Label>
                  <Input id="email" type="email" placeholder="seu@email.com" value={form.email} onChange={set('email')} required />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="whatsapp">WhatsApp (com DDD)</Label>
                  <Input id="whatsapp" placeholder="+55(11)99999-9999" value={form.telefoneWhatsapp} onChange={set('telefoneWhatsapp')} required />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="senha">Senha</Label>
                  <Input id="senha" type="password" placeholder="Mín. 8 chars, 1 número, 1 especial" value={form.senha} onChange={set('senha')} required minLength={8} />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="confirmarSenha">Confirmar senha</Label>
                  <Input id="confirmarSenha" type="password" placeholder="••••••••" value={form.confirmarSenha} onChange={set('confirmarSenha')} required />
                </div>
                <Button type="submit" className="w-full mt-2" disabled={loading}>
                  {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  Criar conta
                </Button>
              </form>
            </CardContent>
          </Card>
        ) : (
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <CheckCircle2 className="h-5 w-5 text-emerald-500" />
                <CardTitle className="text-xl">Verificar WhatsApp</CardTitle>
              </div>
              <CardDescription>
                Enviamos um código de 6 dígitos para <strong>{form.telefoneWhatsapp}</strong>.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleOtp} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="otp">Código OTP</Label>
                  <Input
                    id="otp"
                    placeholder="123456"
                    value={otp}
                    onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                    maxLength={6}
                    className="text-center text-2xl tracking-widest"
                    required
                  />
                </div>
                <Button type="submit" className="w-full" disabled={loading || otp.length < 6}>
                  {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  Verificar
                </Button>
                <Button type="button" variant="ghost" className="w-full" onClick={() => setStep('form')}>
                  Voltar
                </Button>
              </form>
            </CardContent>
          </Card>
        )}

        <p className="text-center text-sm text-muted-foreground">
          Já tem conta?{' '}
          <Link to="/login" className="font-medium text-primary hover:underline">
            Entrar
          </Link>
        </p>
      </div>
    </div>
  )
}
