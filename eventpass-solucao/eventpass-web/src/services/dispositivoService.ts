import api from '@/lib/axios'
import { Dispositivo, PareamentoResponse } from '@/types'
export const dispositivoService = {
  async listar(): Promise<Dispositivo[]> {
    const { data } = await api.get('/dispositivos')
    return data
  },
  async criar(descricao: string): Promise<PareamentoResponse> {
    const { data } = await api.post('/dispositivos', { descricao })
    return data
  },
  async associarEvento(dispositivoId: number, eventoId: number): Promise<void> {
    await api.put(`/dispositivos/${dispositivoId}/evento/${eventoId}`)
  },
  async revogar(id: number): Promise<void> {
    await api.delete(`/dispositivos/${id}`)
  },
}
