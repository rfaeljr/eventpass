import api from '@/lib/axios'
import { Evento, EventoRequest, Page } from '@/types'
export const eventoService = {
  async listar(page = 0, size = 20): Promise<Page<Evento>> {
    const { data } = await api.get('/eventos', { params: { page, size } })
    return data
  },
  async buscar(id: number): Promise<Evento> {
    const { data } = await api.get(`/eventos/${id}`)
    return data
  },
  async criar(payload: EventoRequest): Promise<Evento> {
    const { data } = await api.post('/eventos', payload)
    return data
  },
  async atualizar(id: number, payload: Partial<EventoRequest>): Promise<Evento> {
    const { data } = await api.put(`/eventos/${id}`, payload)
    return data
  },
  async publicar(id: number): Promise<Evento> {
    const { data } = await api.patch(`/eventos/${id}/publicar`)
    return data
  },
  async iniciar(id: number): Promise<Evento> {
    const { data } = await api.patch(`/eventos/${id}/iniciar`)
    return data
  },
  async encerrar(id: number): Promise<Evento> {
    const { data } = await api.patch(`/eventos/${id}/encerrar`)
    return data
  },
  async cancelar(id: number): Promise<Evento> {
    const { data } = await api.patch(`/eventos/${id}/cancelar`)
    return data
  },
  async uploadBanner(id: number, file: File): Promise<Evento> {
    const form = new FormData()
    form.append('banner', file)
    const { data } = await api.post(`/eventos/${id}/banner`, form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return data
  },
}
