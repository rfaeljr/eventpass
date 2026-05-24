import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { Check, Loader2, CreditCard } from 'lucide-react'
import { planoService } from '@/services/planoService'
import { Plano } from '@/types'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { formatarMoeda } from '@/lib/utils'

const TIPO_LABEL: Record<string, string> = {
  MENSAL: 'mês',
  ANUAL: 'ano',
  POR_EVENTO: 'evento',
}

const PLANO_DESTAQUES: Record<string, string[]> = {
  starter: ['1 evento simultâneo', 'Até 50 convidados', '1 dispositivo'],
  mensal: ['Eventos ilimitados', 'Até 500 convidados', '3 dispositivos', 'Relatórios PDF/Excel'],
  anual: ['Eventos ilimitados', 'Até 2.000 convidados', '10 dispositivos', 'Relatórios avançados', 'Suporte prioritário'],
  por_evento: ['Pague apenas ao usar', 'Até 300 convidados por evento', '1 dispositivo por evento'],
}

export default function PlanosPage() {
  const { data: planos = [], isLoading } = useQuery({
    queryKey: ['planos'],
    queryFn: () => planoService.listar(),
  })

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Planos & Assinatura</h1>
        <p className="text-muted-foreground text-sm mt-0.5">
          Escolha o plano ideal para o volume dos seus eventos
        </p>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-16">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
      ) : planos.length === 0 ? (
        <Card>
          <CardContent className="py-12 text-center text-muted-foreground">
            Nenhum plano disponível no momento.
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {(planos as Plano[]).map((plano) => {
            const destaques = PLANO_DESTAQUES[plano.slug] ?? []
            const isAnual = plano.tipoCobranca === 'ANUAL'
            return (
              <Card
                key={plano.id}
                className={isAnual ? 'border-primary ring-2 ring-primary relative' : ''}
              >
                {isAnual && (
                  <div className="absolute -top-3 left-1/2 -translate-x-1/2">
                    <Badge className="px-3">⭐ Mais popular</Badge>
                  </div>
                )}
                <CardHeader className="pb-3">
                  <CardTitle className="text-base">{plano.nome}</CardTitle>
                  <div className="flex items-baseline gap-1">
                    {plano.precoCentavos === 0 ? (
                      <span className="text-2xl font-bold">Grátis</span>
                    ) : (
                      <>
                        <span className="text-2xl font-bold">{formatarMoeda(plano.precoCentavos)}</span>
                        <span className="text-sm text-muted-foreground">/{TIPO_LABEL[plano.tipoCobranca]}</span>
                      </>
                    )}
                  </div>
                </CardHeader>
                <CardContent className="space-y-4">
                  <ul className="space-y-2">
                    {destaques.map((item) => (
                      <li key={item} className="flex items-start gap-2 text-sm">
                        <Check className="h-4 w-4 text-emerald-500 shrink-0 mt-0.5" />
                        {item}
                      </li>
                    ))}
                    {plano.temRelatorios && (
                      <li className="flex items-start gap-2 text-sm">
                        <Check className="h-4 w-4 text-emerald-500 shrink-0 mt-0.5" />
                        Relatórios
                      </li>
                    )}
                  </ul>
                  <Button
                    className="w-full"
                    variant={isAnual ? 'default' : 'outline'}
                    disabled={plano.precoCentavos === 0}
                  >
                    <CreditCard className="mr-2 h-4 w-4" />
                    {plano.precoCentavos === 0 ? 'Plano atual' : 'Assinar'}
                  </Button>
                </CardContent>
              </Card>
            )
          })}
        </div>
      )}

      {/* Payment info */}
      <Card className="bg-muted/40">
        <CardContent className="py-4 px-5">
          <p className="text-sm text-muted-foreground">
            💳 Pagamento via <strong>Mercado Pago</strong> — Cartão de crédito, Pix ou Boleto.
            Cancele a qualquer momento. Em caso de falha no pagamento, 3 dias de carência antes da suspensão.
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
