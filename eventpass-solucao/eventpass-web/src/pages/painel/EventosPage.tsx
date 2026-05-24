import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Plus, Calendar, Loader2, Search } from 'lucide-react'
import { eventoService } from '@/services/eventoService'
import { EventoRequest, Evento } from '@/types'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogTrigger,
} from '@/components/uicat/dialog'
import { useToast } from '@/components/uicat/use-toast'
import { formatarData } from '@/lib/utils'

const STATUS_LABEL: Record<string, string> = {
  RASCUNHO: 'Rascunho', PUBLICADO: 'Publicado',
  EM_ANDAMENTO: 'Em andamento', ENCERRADO: 'Encerrado', CANCELADO: 'Cancelado',
}
const STATUS_VARIANT: Record<string, any> = {
  RASCUNHO: 'secondary', PUBLICADO: 'default',
  EM_ANDAMENTO: 'success', ENCERRADO: 'outline', CANCELADO: 'destructive',
}

const EMPTY_FORM: EventoRequest = {
  nome: '', iniciaEm: '', terminaEm: '', descricao: '', local: '',
  capacidadeMaxima: undefined, maxAcompanhantes: 0, permiteReentrada: false,
}

export default function EventosPage() {
  const qc = useQueryClient()
  const { toast } = useToast()
  const [open, setOpen] = useState(false)
  const [busca, setBusca] = useState('')
  const [form, setForm] = useState<EventoRequest>(EMPTY_FORM)

  const { data: page, isLoading } = useQuery({
    queryKey: ['eventos'],
    queryFn: () => eventoService.listar(0, 50),
  })

  const criar = useMutation({
    mutationFn: (payload: EventoRequest) => eventoService.criar(payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['eventos'] })
      setOpen(false)
      setForm(EMPTY_FORM)
      toast({ title: 'Evento criado!', description: 'O evento foi criado como rascunho.' })
    },
    onError: (err: any) => {
      toast({ title: 'Erro', description: err?.response?.data?.mensagem ?? 'Não foi possível criar o evento.', variant: 'destructive' })
    },
  })

  function set(field: keyof EventoRequest) {
    return (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.type === 'number' ? Number(e.target.value) : e.target.type === 'checkbox' ? e.target.checked : e.target.value
      setForm((prev) => ({ ...prev, [field]: value }))
    }
  }

  const eventos = (page?.content ?? []).filter((e: Evento) =>
    e.nome.toLowerCase().includes(busca.toLowerCase())
  )

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Eventos</h1>
          <p className="text-muted-foreground text-sm mt-0.5">Gerencie todos os seus eventos</p>
        </div>
        <Dialog open={open} onOpenChange={setOpen}>
          <DialogTrigger asChild>
            <Button><Plus className="mr-2 h-4 w-4" />Novo Evento</Button>
          </DialogTrigger>
          <DialogContent className="max-w-md">
            <DialogHeader>
              <DialogTitle>Criar Evento</DialogTitle>
            </DialogHeader>
            <form
              onSubmit={(e) => { e.preventDefault(); criar.mutate(form) }}
              className="space-y-3 mt-2"
            >
              <div className="space-y-1.5">
                <Label>Nome do evento *</Label>
                <Input placeholder="Ex: Festa de Aniversário" value={form.nome} onChange={set('nome')} required />
              </div>
              <div className="space-y-1.5">
                <Label>Local</Label>
                <Input placeholder="Endereço ou nome do local" value={form.local ?? ''} onChange={set('local')} />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label>Início *</Label>
                  <Input type="datetime-local" value={form.iniciaEm} onChange={set('iniciaEm')} required />
                </div>
                <div className="space-y-1.5">
                  <Label>Término *</Label>
                  <Input type="datetime-local" value={form.terminaEm} onChange={set('terminaEm')} required />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label>Capacidade máxima</Label>
                  <Input type="number" min={0} placeholder="Ilimitado" value={form.capacidadeMaxima ?? ''} onChange={set('capacidadeMaxima')} />
                </div>
                <div className="space-y-1.5">
                  <Label>Máx. acompanhantes</Label>
                  <Input type="number" min={0} max={10} value={form.maxAcompanhantes ?? 0} onChange={set('maxAcompanhantes')} />
                </div>
              </div>
              <DialogFooter className="pt-2">
                <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
                <Button type="submit" disabled={criar.isPending}>
                  {criar.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  Criar
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      {/* Search */}
      <div className="relative max-w-sm">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          className="pl-9"
          placeholder="Buscar eventos..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
      </div>

      {/* List */}
      {isLoading ? (
        <div className="flex justify-center py-16">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
      ) : eventos.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-16 gap-3">
            <Calendar className="h-12 w-12 text-muted-foreground/30" />
            <p className="text-muted-foreground">Nenhum evento encontrado.</p>
            <Button onClick={() => setOpen(true)}><Plus className="mr-2 h-4 w-4" />Criar evento</Button>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-3">
          {eventos.map((evento: Evento) => (
            <Link key={evento.id} to={`/painel/eventos/${evento.id}`}>
              <Card className="hover:shadow-md transition-shadow cursor-pointer">
                <CardHeader className="py-4 px-5">
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex-1 min-w-0">
                      <CardTitle className="text-base truncate">{evento.nome}</CardTitle>
                      <div className="flex items-center gap-4 mt-1 text-xs text-muted-foreground">
                        <span>📅 {formatarData(evento.iniciaEm)}</span>
                        {evento.local && <span>📍 {evento.local}</span>}
                        {evento.capacidadeMaxima && <span>👥 até {evento.capacidadeMaxima}</span>}
                      </div>
                    </div>
                    <Badge variant={STATUS_VARIANT[evento.status]}>{STATUS_LABEL[evento.status]}</Badge>
                  </div>
                </CardHeader>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
