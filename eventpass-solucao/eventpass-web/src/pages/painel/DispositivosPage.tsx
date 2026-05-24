import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, Loader2, Smartphone, Trash2 } from 'lucide-react'
import { QRCodeSVG } from 'qrcode.react'
import { dispositivoService } from '@/services/dispositivoService'
import { Dispositivo, PareamentoResponse } from '@/types'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogTrigger,
} from '@/components/uicat/dialog'
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import { useToast } from '@/components/uicat/use-toast'
import { formatarData } from '@/lib/utils'

export default function DispositivosPage() {
  const qc = useQueryClient()
  const { toast } = useToast()
  const [openAdd, setOpenAdd] = useState(false)
  const [descricao, setDescricao] = useState('')
  const [pareamento, setPareamento] = useState<PareamentoResponse | null>(null)

  const { data: dispositivos = [], isLoading } = useQuery({
    queryKey: ['dispositivos'],
    queryFn: () => dispositivoService.listar(),
  })

  const criar = useMutation({
    mutationFn: (desc: string) => dispositivoService.criar(desc),
    onSuccess: (data) => {
      qc.invalidateQueries({ queryKey: ['dispositivos'] })
      setDescricao('')
      setPareamento(data)
    },
    onError: (err: any) => {
      toast({ title: 'Erro', description: err?.response?.data?.mensagem, variant: 'destructive' })
    },
  })

  const revogar = useMutation({
    mutationFn: (id: number) => dispositivoService.revogar(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['dispositivos'] })
      toast({ title: 'Dispositivo desvinculado.' })
    },
  })

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Dispositivos</h1>
          <p className="text-muted-foreground text-sm mt-0.5">Apps Android vinculados ao seu painel</p>
        </div>

        <Dialog open={openAdd} onOpenChange={(v) => { setOpenAdd(v); if (!v) { setPareamento(null) } }}>
          <DialogTrigger asChild>
            <Button><Plus className="mr-2 h-4 w-4" />Adicionar Dispositivo</Button>
          </DialogTrigger>
          <DialogContent className="max-w-md">
            <DialogHeader>
              <DialogTitle>
                {pareamento ? 'QR Code de Pareamento' : 'Novo Dispositivo'}
              </DialogTitle>
            </DialogHeader>

            {!pareamento ? (
              <form
                onSubmit={(e) => { e.preventDefault(); criar.mutate(descricao) }}
                className="space-y-4 mt-2"
              >
                <div className="space-y-1.5">
                  <Label>Nome / descrição *</Label>
                  <Input
                    placeholder='Ex: "Portaria Principal", "Entrada VIP"'
                    value={descricao}
                    onChange={(e) => setDescricao(e.target.value)}
                    required
                  />
                </div>
                <DialogFooter>
                  <Button type="button" variant="outline" onClick={() => setOpenAdd(false)}>Cancelar</Button>
                  <Button type="submit" disabled={criar.isPending}>
                    {criar.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                    Gerar QR Code
                  </Button>
                </DialogFooter>
              </form>
            ) : (
              <div className="flex flex-col items-center gap-4 py-2">
                {/* Pairing steps */}
                <div className="w-full bg-muted rounded-lg p-3 text-sm space-y-1 text-muted-foreground">
                  <p className="font-semibold text-foreground mb-2">Como vincular:</p>
                  <p>1. Abra o app <strong>EventPass Portaria</strong> no Android</p>
                  <p>2. Toque em <strong>"Vincular ao painel"</strong></p>
                  <p>3. Aponte a câmera para o QR Code abaixo</p>
                </div>

                <div className="bg-white p-4 rounded-xl shadow-md">
                  <QRCodeSVG
                    value={pareamento.tokenPareamento}
                    size={200}
                    level="M"
                  />
                </div>

                <div className="text-center space-y-1">
                  <p className="font-medium">{pareamento.descricao}</p>
                  <p className="text-xs text-muted-foreground">
                    ⏱ Expira em: {formatarData(pareamento.expiraEm)}
                  </p>
                  <p className="text-xs text-amber-600 dark:text-amber-400 font-medium">
                    Uso único — 10 minutos para escanear
                  </p>
                </div>

                <Button className="w-full" onClick={() => setOpenAdd(false)}>Fechar</Button>
              </div>
            )}
          </DialogContent>
        </Dialog>
      </div>

      {/* How it works banner */}
      <div className="rounded-lg border border-primary/20 bg-primary/5 px-4 py-3">
        <p className="text-sm font-medium text-primary mb-1">Como funciona o pareamento?</p>
        <p className="text-xs text-muted-foreground leading-relaxed">
          Clique em "Adicionar Dispositivo" → preencha o nome → um QR Code aparecerá na tela →
          abra o app <strong>EventPass Portaria</strong> no celular e escaneie. O dispositivo fica
          vinculado permanentemente com um Device Token seguro.
        </p>
      </div>

      {/* List */}
      {isLoading ? (
        <div className="flex justify-center py-16">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
      ) : (dispositivos as Dispositivo[]).length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-16 gap-3">
            <Smartphone className="h-12 w-12 text-muted-foreground/30" />
            <p className="text-muted-foreground">Nenhum dispositivo vinculado ainda.</p>
            <Button onClick={() => setOpenAdd(true)}><Plus className="mr-2 h-4 w-4" />Adicionar</Button>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-3">
          {(dispositivos as Dispositivo[]).map((d) => (
            <Card key={d.id}>
              <CardHeader className="py-4 px-5">
                <div className="flex items-center justify-between gap-4">
                  <div className="flex items-center gap-3">
                    <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-muted">
                      <Smartphone className="h-5 w-5 text-muted-foreground" />
                    </div>
                    <div>
                      <p className="font-semibold text-sm">{d.descricao}</p>
                      <p className="text-xs text-muted-foreground">
                        {d.modeloAndroid ?? 'Android'}
                        {d.versaoApp ? ` · v${d.versaoApp}` : ''}
                        {d.ultimoAcessoEm ? ` · Último acesso: ${formatarData(d.ultimoAcessoEm)}` : ''}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2 shrink-0">
                    <Badge variant={d.status === 'ATIVO' ? 'success' : d.status === 'AGUARDANDO' ? 'warning' : 'secondary'}>
                      {d.status === 'ATIVO' ? '🟢 Online' : d.status === 'AGUARDANDO' ? '⏳ Aguardando' : '⚫ Revogado'}
                    </Badge>
                    <AlertDialog>
                      <AlertDialogTrigger asChild>
                        <Button size="icon" variant="ghost" className="text-destructive hover:text-destructive">
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </AlertDialogTrigger>
                      <AlertDialogContent>
                        <AlertDialogHeader>
                          <AlertDialogTitle>Desvincular dispositivo?</AlertDialogTitle>
                          <AlertDialogDescription>
                            O app <strong>{d.descricao}</strong> perderá acesso ao painel imediatamente.
                            Esta ação não pode ser desfeita.
                          </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                          <AlertDialogCancel>Cancelar</AlertDialogCancel>
                          <AlertDialogAction
                            className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                            onClick={() => revogar.mutate(d.id)}
                          >
                            Desvincular
                          </AlertDialogAction>
                        </AlertDialogFooter>
                      </AlertDialogContent>
                    </AlertDialog>
                  </div>
                </div>
              </CardHeader>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
