import api from '@/lib/axios'
import { Plano } from '@/types'
export const planoService = {
  async listar(): Promise<Plano[]> {
    const { data } = await api.get('/planos')
    return data
  },
}
