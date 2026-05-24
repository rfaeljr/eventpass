import api from '@/lib/axios'
import { DashboardResumo, EntradaItem } from '@/types'
export const dashboardService = {
  async resumo(eventoId: number): Promise<DashboardResumo> {
    const { data } = await api.get(`/eventos/${eventoId}/dashboard`)
    return data
  },
  async entradas(eventoId: number): Promise<EntradaItem[]> {
    const { data } = await api.get(`/eventos/${eventoId}/dashboard/entradas`)
    return data
  },
}
