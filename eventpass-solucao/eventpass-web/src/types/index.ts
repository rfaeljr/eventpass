// ── Auth ──────────────────────────────────────────────────────────────────────
export interface AuthResponse {
  token: string
  nome: string
  email: string
  emTrial: boolean
}

export interface Cliente {
  id: number
  nome: string
  email: string
  telefoneWhatsapp: string
  status: 'ATIVO' | 'SUSPENSO' | 'CANCELADO'
  trialExpiraEm?: string
}

// ── Planos ────────────────────────────────────────────────────────────────────
export interface Plano {
  id: number
  slug: string
  nome: string
  tipoCobranca: 'MENSAL' | 'ANUAL' | 'POR_EVENTO'
  precoCentavos: number
  maxEventos?: number
  maxConvidadosPorEvento?: number
  maxDispositivos: number
  temRelatorios: boolean
  ativo: boolean
}

// ── Eventos ───────────────────────────────────────────────────────────────────
export type StatusEvento = 'RASCUNHO' | 'PUBLICADO' | 'EM_ANDAMENTO' | 'ENCERRADO' | 'CANCELADO'

export interface Evento {
  id: number
  uuid: string
  nome: string
  descricao?: string
  local?: string
  urlBanner?: string
  iniciaEm: string
  terminaEm: string
  capacidadeMaxima?: number
  maxAcompanhantes: number
  permiteReentrada: boolean
  status: StatusEvento
  criadoEm: string
}

export interface EventoRequest {
  nome: string
  descricao?: string
  local?: string
  iniciaEm: string
  terminaEm: string
  capacidadeMaxima?: number
  maxAcompanhantes?: number
  permiteReentrada?: boolean
}

// ── Convidados ────────────────────────────────────────────────────────────────
export type StatusConvite = 'PENDENTE' | 'ENVIADO' | 'CONFIRMADO' | 'RECUSADO'

export interface Convidado {
  id: number
  uuid: string
  nome: string
  documento?: string
  telefone?: string
  email?: string
  grupoTag?: string
  maxAcompanhantes: number
  urlQrcode?: string
  statusConvite: StatusConvite
  conviteEnviadoEm?: string
  criadoEm: string
}

export interface ConvidadoRequest {
  nome: string
  documento?: string
  telefone?: string
  email?: string
  grupoTag?: string
  maxAcompanhantes?: number
}

// ── Dispositivos ──────────────────────────────────────────────────────────────
export type StatusDispositivo = 'AGUARDANDO' | 'ATIVO' | 'REVOGADO'

export interface Dispositivo {
  id: number
  descricao: string
  modeloAndroid?: string
  versaoApp?: string
  status: StatusDispositivo
  ultimoAcessoEm?: string
  criadoEm: string
}

export interface PareamentoResponse {
  dispositivoId: number
  descricao: string
  tokenPareamento: string
  expiraEm: string
}

// ── Dashboard ─────────────────────────────────────────────────────────────────
export interface DashboardResumo {
  eventoNome: string
  eventoStatus: string
  totalEntradas: number
  totalConvidados: number
  totalAcompanhantes: number
  totalNegadas: number
  capacidadeMaxima?: string
  percentualOcupacao: string
}

export interface EntradaItem {
  id: number
  convidadoNome: string
  grupoTag?: string
  status: 'LIBERADO' | 'NEGADO' | 'REENTRADA'
  qtdAcompanhantes: number
  registradoEm: string
}

// ── Paginação ─────────────────────────────────────────────────────────────────
export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

// ── Erro API ──────────────────────────────────────────────────────────────────
export interface ErroAPI {
  status: number
  erro: string
  mensagem: string
  timestamp: string
}