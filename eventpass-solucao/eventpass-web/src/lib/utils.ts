import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatarData(data: string): string {
  return new Date(data).toLocaleDateString('pt-BR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}

export function formatarMoeda(centavos: number): string {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency', currency: 'BRL'
  }).format(centavos / 100)
}

export function formatarCPF(cpf: string): string {
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}

export function formatarTelefone(tel: string): string {
  const n = tel.replace(/\D/g, '')
  if (n.length === 13) return `+${n.slice(0,2)} (${n.slice(2,4)}) ${n.slice(4,9)}-${n.slice(9)}`
  return tel
}

export function iniciais(nome: string): string {
  return nome.split(' ').slice(0,2).map(p => p[0]).join('').toUpperCase()
}