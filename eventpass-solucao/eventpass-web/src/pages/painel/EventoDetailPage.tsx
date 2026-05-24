import React, { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  ArrowLeft, Loader2, Plus, Upload, Send, Trash2, QrCode, RefreshCw,
} from 'lucide-react'
import { eventoService } from '@/services/eventoService'
import { convidadoService } from '@/services/convidadoService'
import { dashboardService } from '@/services/dashboardService'
import { dispositivoService } from '@/services/dispositivoService'
import { Convidado, ConvidadoRequest, Dispositivo } from '@/types'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Progress } from '@/components/ui/progress'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogTrigger,
} from '@/components/uicat/dialog'
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/uicat/table'
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
const CONV_LABEL: Record<string, string> = {
  PENDENTE: 'Pendente', ENVIADO: 'Enviado', CONFIRMADO: 'Confirmado', RECUSADO: 'Recusado',
}

const EMPTY_CONV: ConvidadoRequest = { nome: '', documento: '', telefone: '', email: '', grupoTag: '', maxAcompanhantes: 0 }

export default function EventoDetailPage() {
  const { id } = useParams<{ id: string }>()
  const eventoId = Number(id)
  const navigate = useNavigate()
  const qc = useQueryClient()
  const { toast } = useToast()
  const [openConv, setOpenConv] = useState(false)
  const [convForm, setConvForm] = useState<ConvidadoRequest>(EMPTY_CONV)

  const { data: evento, isLoading } = useQuery({
    queryKey: ['evento', eventoId],
    queryFn: () => eventoService.buscar(eventoId),
    enabled: !!eventoId,
  })

  const { data: convPage } = useQuery({
    queryKey: ['convidados', eventoId],
    queryFn: () => convidadoService.listar(eventoId),
    enabled: !!eventoId,
  })

  const { data: resumo } = useQuery({
    queryKey: ['dashboard', eventoId],
    queryFn: () => dashboardService.resumo(eventoId),
    enabled: !!eventoId,
    refetchInterval: 10_000,
  })

  const { data: entradas } = useQuery({
    queryKey: ['entradas', eventoId],
    queryFn: () => dashboardService.entradas(eventoId),
    enabled: !!eventoId,
    refetchInterval: 10_000,
  })

  const { data: dispositivos } = useQuery({
    queryKey: ['dispositivos'],
    queryFn: () => dispositivoService.listar(),
  })

  const criarConv = useMutation({
    mutationFn: (p: ConvidadoRequest) => convidadoService.criar(eventoId, p),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['convidados', eventoId] })
      setOpenConv(false)
      setConvForm(EMPTY_CONV)
      toast({ title: 'Convidado adicionado!' })
    },
    onError: (err: any) => {
      toast({ title: 'Erro', description: err?.response?.data?.mensagem, variant: 'destructive' })
    },
  })

  const excluirConv = useMutation({
    mutationFn: (cId: number) => convidadoService.excluir(eventoId, cId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['convidados', eventoId] })
      toast({ title: 'Convidado removido.' })
    },
  })

  const enviarConvites = useMutation({
    mutationFn: () => convidadoService.enviarConvites(eventoId),
    onSuccess: () => toast({ title: 'Envio enfileirado!', description: 'Os convites serão enviados em breve.' }),
  })

  const associarDevice = useMutation({
    mutationFn: (dId: number) => dispositivoService.associarEvento(dId, eventoId),
    onSuccess: () => toast({ title: 'Dispositivo associado ao evento!' }),
  })

  const mudarStatus = useMutation({
    mutationFn: (acao: 'publicar' | 'iniciar' | 'encerrar' | 'cancelar') =>
      eventoService[acao](eventoId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['evento', eventoId] }),
    onError: (err: any) => {
      toast({ title: 'Erro', description: err?.response?.data?.mensagem, variant: 'destructive' })
    },
  })

  function setConv(field: keyof ConvidadoRequest) {
    return (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.type === 'number' ? Number(e.target.value) : e.target.value
      setConvForm((prev) => ({ ...prev, [field]: value }))
    }
  }

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-full py-32">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    )
  }

  if (!evento) return null

  const convidados = convPage?.content ?? []
  const ocupacao = resumo ? Math.round(parseFloat(resumo.percentualOcupacao)) : 0

  return (
    <div className="p-6 space-y-5">
      {/* Header */}
      <div>
        <button
          onClick={() => navigate('/painel/eventos')}
          className="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground mb-3"
        >
          <ArrowLeft className="h-4 w-4" /> Voltar
        </button>
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold">{evento.nome}</h1>
              <Badge variant={STATUS_VARIANT[evento.status]}>{STATUS_LABEL[evento.status]}</Badge>
            </div>
            <p className="text-sm text-muted-foreground mt-0.5">
              {formatarData(evento.iniciaEm)}
              {evento.local && ` · ${evento.local}`}
            </p>
          </div>
          {/* Status actions */}
          <div className="flex flex-wrap gap-2">
            {evento.status === 'RASCUNHO' && (
              <Button size="sm" onClick={() => mudarStatus.mutate('publicar')} disabled={mudarStatus.isPending}>
                Publicar
              </Button>
            )}
            {evento.status === 'PUBLICADO' && (
              <Button size="sm" onClick={() => mudarStatus.mutate('iniciar')} disabled={mudarStatus.isPending}>
                Iniciar evento
              </Button>
            )}
            {evento.status === 'EM_ANDAMENTO' && (
              <Button size="sm" variant="secondary" onClick={() => mudarStatus.mutate('encerrar')} disabled={mudarStatus.isPending}>
                Encerrar
              </Button>
            )}
            {!['ENCERRADO', 'CANCELADO'].includes(evento.status) && (
              <Button size="sm" variant="destructive" onClick={() => mudarStatus.mutate('cancelar')} disabled={mudarStatus.isPending}>
                Cancelar
              </Button>
            )}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <Tabs defaultValue="convidados">
        <TabsList>
          <TabsTrigger value="convidados">Convidados ({convidados.length})</TabsTrigger>
          <TabsTrigger value="dispositivos">Dispositivos</TabsTrigger>
          <TabsTrigger value="dashboard">Dashboard ao vivo</TabsTrigger>
        </TabsList>

        {/* ── TAB CONVIDADOS ── */}
        <TabsContent value="convidados" className="space-y-4 pt-2">
          <div className="flex flex-wrap gap-2">
            <Dialog open={openConv} onOpenChange={setOpenConv}>
              <DialogTrigger asChild>
                <Button size="sm"><Plus className="mr-1.5 h-4 w-4" />Adicionar</Button>
              </DialogTrigger>
              <DialogContent className="max-w-md">
                <DialogHeader><DialogTitle>Novo convidado</DialogTitle></DialogHeader>
                <form onSubmit={(e) => { e.preventDefault(); criarConv.mutate(convForm) }} className="space-y-3 mt-2">
                  <div className="space-y-1.5">
                    <Label>Nome completo *</Label>
                    <Input value={convForm.nome} onChange={setConv('nome')} required />
                  </div>
                  <div className="grid grid-cols-2 gap-3">
                    <div className="space-y-1.5">
                      <Label>CPF / RG</Label>
                      <Input value={convForm.documento ?? ''} onChange={setConv('documento')} />
                    </div>
                    <div className="space-y-1.5">
                      <Label>WhatsApp</Label>
                      <Input value={convForm.telefone ?? ''} onChange={setConv('telefone')} />
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-3">
                    <div className="space-y-1.5">
                      <Label>E-mail</Label>
                      <Input type="email" value={convForm.email ?? ''} onChange={setConv('email')} />
                    </div>
                    <div className="space-y-1.5">
                      <Label>Grupo / Mesa</Label>
                      <Input value={convForm.grupoTag ?? ''} onChange={setConv('grupoTag')} />
                    </div>
                  </div>
                  <div className="space-y-1.5">
                    <Label>Máx. acompanhantes</Label>
                    <Input type="number" min={0} max={10} value={convForm.maxAcompanhantes} onChange={setConv('maxAcompanhantes')} />
                  </div>
                  <DialogFooter className="pt-2">
                    <Button type="button" variant="outline" onClick={() => setOpenConv(false)}>Cancelar</Button>
                    <Button type="submit" disabled={criarConv.isPending}>
                      {criarConv.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                      Adicionar
                    </Button>
                  </DialogFooter>
                </form>
              </DialogContent>
            </Dialog>

            <Button size="sm" variant="outline" disabled={enviarConvites.isPending || convidados.length === 0} onClick={() => enviarConvites.mutate()}>
              {enviarConvites.isPending
                ? <Loader2 className="mr-1.5 h-4 w-4 animate-spin" />
                : <Send className="mr-1.5 h-4 w-4" />}
              Enviar convites
            </Button>
          </div>

          {convidados.length === 0 ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12 text-center gap-2">
                <p className="text-muted-foreground">Nenhum convidado cadastrado.</p>
                <p className="text-xs text-muted-foreground">Adicione manualmente ou importe uma planilha.</p>
              </CardContent>
            </Card>
          ) : (
            <Card>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Nome</TableHead>
                    <TableHead>Documento</TableHead>
                    <TableHead>Contato</TableHead>
                    <TableHead>Grupo</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="text-right">Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {convidados.map((c: Convidado) => (
                    <TableRow key={c.id}>
                      <TableCell className="font-medium">{c.nome}</TableCell>
                      <TableCell className="text-muted-foreground text-sm">{c.documento ?? '—'}</TableCell>
                      <TableCell className="text-sm">{c.telefone ?? c.email ?? '—'}</TableCell>
                      <TableCell className="text-sm">{c.grupoTag ?? '—'}</TableCell>
                      <TableCell>
                        <Badge variant="outline" className="text-xs">{CONV_LABEL[c.statusConvite]}</Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <div className="flex items-center justify-end gap-1">
                          <Button size="icon" variant="ghost" asChild title="Ver QR Code">
                            <a href={convidadoService.urlQrCode(eventoId, c.id)} target="_blank" rel="noreferrer">
                              <QrCode className="h-4 w-4" />
                            </a>
                          </Button>
                          <Button
                            size="icon" variant="ghost"
                            className="text-destructive hover:text-destructive"
                            onClick={() => excluirConv.mutate(c.id)}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Card>
          )}
        </TabsContent>

        {/* ── TAB DISPOSITIVOS ── */}
        <TabsContent value="dispositivos" className="space-y-4 pt-2">
          <p className="text-sm text-muted-foreground">
            Associe um dispositivo para liberar a leitura de QR Codes neste evento.
          </p>
          {(dispositivos ?? []).length === 0 ? (
            <Card>
              <CardContent className="py-10 text-center text-muted-foreground text-sm">
                Nenhum dispositivo vinculado.{' '}
                <a href="/painel/dispositivos" className="text-primary underline">Adicionar dispositivo</a>
              </CardContent>
            </Card>
          ) : (
            <div className="grid gap-3">
              {(dispositivos as Dispositivo[]).map((d) => (
                <Card key={d.id}>
                  <CardHeader className="py-3 px-5">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-medium text-sm">{d.descricao}</p>
                        <p className="text-xs text-muted-foreground">
                          {d.modeloAndroid ?? 'Android'} · v{d.versaoApp ?? '—'}
                        </p>
                      </div>
                      <div className="flex items-center gap-2">
                        <Badge variant={d.status === 'ATIVO' ? 'success' : 'secondary'}>
                          {d.status === 'ATIVO' ? '🟢 Online' : '⚪ Aguardando'}
                        </Badge>
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => associarDevice.mutate(d.id)}
                          disabled={associarDevice.isPending}
                        >
                          Associar
                        </Button>
                      </div>
                    </div>
                  </CardHeader>
                </Card>
              ))}
            </div>
          )}
        </TabsContent>

        {/* ── TAB DASHBOARD AO VIVO ── */}
        <TabsContent value="dashboard" className="space-y-4 pt-2">
          {resumo ? (
            <>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
                <Card>
                  <CardHeader className="pb-1">
                    <p className="text-xs text-muted-foreground">Entradas</p>
                    <p className="text-2xl font-bold">{resumo.totalEntradas}</p>
                  </CardHeader>
                </Card>
                <Card>
                  <CardHeader className="pb-1">
                    <p className="text-xs text-muted-foreground">Acompanhantes</p>
                    <p className="text-2xl font-bold">{resumo.totalAcompanhantes}</p>
                  </CardHeader>
                </Card>
                <Card>
                  <CardHeader className="pb-1">
                    <p className="text-xs text-muted-foreground">Convidados</p>
                    <p className="text-2xl font-bold">{resumo.totalConvidados}</p>
                  </CardHeader>
                </Card>
                <Card>
                  <CardHeader className="pb-1">
                    <p className="text-xs text-muted-foreground">Negadas</p>
                    <p className="text-2xl font-bold text-destructive">{resumo.totalNegadas}</p>
                  </CardHeader>
                </Card>
              </div>

              {resumo.capacidadeMaxima && (
                <Card>
                  <CardHeader className="pb-2">
                    <div className="flex items-center justify-between">
                      <CardTitle className="text-sm font-medium">Ocupação</CardTitle>
                      <span className="text-sm font-bold">{resumo.percentualOcupacao}%</span>
                    </div>
                  </CardHeader>
                  <CardContent className="pt-0">
                    <Progress value={ocupacao} className="h-3" />
                    <p className="text-xs text-muted-foreground mt-2">
                      {resumo.totalEntradas} / {resumo.capacidadeMaxima} pessoas
                    </p>
                  </CardContent>
                </Card>
              )}

              {/* Últimas entradas */}
              <Card>
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <CardTitle className="text-sm font-medium">Últimas entradas</CardTitle>
                    <Badge variant="outline" className="text-xs">Atualiza a cada 10s</Badge>
                  </div>
                </CardHeader>
                {(entradas ?? []).length === 0 ? (
                  <CardContent><p className="text-sm text-muted-foreground">Nenhuma entrada registrada ainda.</p></CardContent>
                ) : (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Convidado</TableHead>
                        <TableHead>Grupo</TableHead>
                        <TableHead>Acompanhantes</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead>Horário</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {(entradas ?? []).slice(0, 20).map((e) => (
                        <TableRow key={e.id}>
                          <TableCell className="font-medium">{e.convidadoNome}</TableCell>
                          <TableCell className="text-sm text-muted-foreground">{e.grupoTag ?? '—'}</TableCell>
                          <TableCell>{e.qtdAcompanhantes}</TableCell>
                          <TableCell>
                            <Badge variant={e.status === 'LIBERADO' ? 'success' : e.status === 'NEGADO' ? 'destructive' : 'warning'} className="text-xs">
                              {e.status}
                            </Badge>
                          </TableCell>
                          <TableCell className="text-sm text-muted-foreground">{formatarData(e.registradoEm)}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                )}
              </Card>
            </>
          ) : (
            <div className="flex justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
            </div>
          )}
        </TabsContent>
      </Tabs>
    </div>
  )
}
