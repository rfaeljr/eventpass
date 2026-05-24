import api from '@/lib/axios'
import { Convidado, ConvidadoRequest, Page } from '@/types'
export const convidadoService = {
  async listar(eventoId: number, page = 0, size = 50): Promise<Page<Convidado>> {
    const { data } = await api.get(`/eventos/${eventoId}/convidados`, {
      params: { page, size }
    })
    return data
  },
  async criar(eventoId: number, payload: ConvidadoRequest): Promise<Convidado> {
    const { data } = await api.post(`/eventos/${eventoId}/convidados`, payload)
    return data
  },
  async importarPlanilha(eventoId: number, arquivo: File) {
    const form = new FormData()
    form.append('arquivo', arquivo)
    const { data } = await api.post(
      `/eventos/${eventoId}/convidados/importar`, form,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    )
    return data
  },
  async enviarConvites(eventoId: number, convidadoIds?: number[]): Promise<void> {
    await api.post(`/eventos/${eventoId}/convidados/enviar-convites`, convidadoIds)
  },
  async excluir(eventoId: number, convidadoId: number): Promise<void> {
    await api.delete(`/eventos/${eventoId}/convidados/${convidadoId}`)
  },
  urlQrCode(eventoId: number, convidadoId: number): string {
    return `/api/eventos/${eventoId}/convidados/${convidadoId}/qrcode`
  },
}
