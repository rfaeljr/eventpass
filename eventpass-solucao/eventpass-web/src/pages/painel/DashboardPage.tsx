import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { Calendar, Users, Smartphone, TrendingUp, Plus } from 'lucide-react'
import { Link } from 'react-router-dom'
import { useAuth } from '@/store/AuthContext'
import { eventoService } from '@/services/eventoService'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { formatarData } from '@/lib/utils'
import { Evento } from '@/types'

const STATUS_LABEL: Record<string, string> = {
  RASCUNHO: 'Rascunho',
  PUBLICADO: 'Publicado',
  EM_ANDAMENTO: 'Em andamento',
  ENCERRADO: 'Encerrado',
  CANCELADO: 'Cancelado',
}

const STATUS_VARIANT: Record<string, 'default' | 'secondary' | 'success' | 'warning' | 'destructive' | 'outline'> = {
  RASCUNHO: 'secondary',
  PUBLICADO: 'default',
  EM_ANDAMENTO: 'success',
  ENCERRADO: 'outline',
  CANCELADO: 'destructive',
}

export default function DashboardPage() {
  const { cliente, emTrial } = useAuth()
  const { data: page } = useQuery({
    queryKey: ['eventos'],
    queryFn: () => eventoService.listar(0, 5),
  })

  const eventos = page?.content ?? []
  const proximos = eventos.filter((e) => ['PUBLICADO', 'EM_ANDAMENTO'].includes(e.status))

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Olá, {cliente?.nome?.split(' ')[0]} 👋</h1>
          <p className="text-muted-foreground text-sm mt-0.5">
            Bem-vindo ao seu painel EventPass
          </p>
        </div>
        <Button asChild>
          <Link to="/painel/eventos">
            <Plus className="mr-2 h-4 w-4" />
            Novo Evento
          </Link>
        </Button>
      </div>

      {/* Trial banner */}
      {emTrial && (
        <div className="rounded-lg border border-amber-300 bg-amber-50 dark:bg-amber-950/20 dark:border-amber-800 px-4 py-3 flex items-center justify-between">
          <p className="text-sm text-amber-800 dark:text-amber-300 font-medium">
            🎉 Você está no período trial — acesso completo por 14 dias.
          </p>
          <Button size="sm" variant="outline" asChild>
            <Link to="/painel/planos">Ver planos</Link>
          </Button>
        </div>
      )}

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardDescription className="flex items-center gap-2">
              <Calendar className="h-4 w-4" /> Eventos
            </CardDescription>
            <CardTitle className="text-3xl">{page?.totalElements ?? '—'}</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-xs text-muted-foreground">total criados</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardDescription className="flex items-center gap-2">
              <TrendingUp className="h-4 w-4" /> Ativos agora
            </CardDescription>
            <CardTitle className="text-3xl">{proximos.length}</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-xs text-muted-foreground">publicados ou em andamento</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardDescription className="flex items-center gap-2">
              <Smartphone className="h-4 w-4" /> Dispositivos
            </CardDescription>
            <CardTitle className="text-3xl">
              <Link to="/painel/dispositivos" className="hover:underline">Gerenciar →</Link>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-xs text-muted-foreground">apps de portaria vinculados</p>
          </CardContent>
        </Card>
      </div>

      {/* Recent events */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="text-base">Eventos recentes</CardTitle>
            <Button variant="ghost" size="sm" asChild>
              <Link to="/painel/eventos">Ver todos</Link>
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          {eventos.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 text-center gap-3">
              <Calendar className="h-10 w-10 text-muted-foreground/40" />
              <p className="text-muted-foreground text-sm">Nenhum evento criado ainda.</p>
              <Button asChild size="sm">
                <Link to="/painel/eventos"><Plus className="mr-2 h-4 w-4" />Criar primeiro evento</Link>
              </Button>
            </div>
          ) : (
            <div className="divide-y">
              {eventos.map((evento: Evento) => (
                <Link
                  key={evento.id}
                  to={`/painel/eventos/${evento.id}`}
                  className="flex items-center justify-between py-3 hover:bg-muted/40 px-2 rounded transition-colors"
                >
                  <div>
                    <p className="font-medium text-sm">{evento.nome}</p>
                    <p className="text-xs text-muted-foreground">{formatarData(evento.iniciaEm)}</p>
                  </div>
                  <Badge variant={STATUS_VARIANT[evento.status] ?? 'outline'}>
                    {STATUS_LABEL[evento.status] ?? evento.status}
                  </Badge>
                </Link>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
