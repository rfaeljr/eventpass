# 🎉 EventPass — Backend Spring Boot

SaaS de controle de acesso em eventos via QR Code.

## 🗂️ Estrutura do Projeto

```
src/main/java/br/com/eventpass/
├── EventpassApplication.java
├── api/
│   ├── controller/          # REST Controllers
│   └── handler/             # GlobalExceptionHandler
├── application/
│   ├── dto/
│   │   ├── request/         # Records de entrada
│   │   └── response/        # Records de saída
│   └── service/             # Regras de negócio
├── config/                  # Security, WebSocket, Redis, Async
├── domain/
│   ├── entity/              # Entidades JPA
│   ├── enums/               # Enumerações
│   └── repository/          # Interfaces JPA Repository
└── infrastructure/
    ├── qrcode/              # Geração QR Code (ZXing)
    ├── planilha/            # Import Excel/CSV (Apache POI)
    ├── security/            # Filtros JWT e Device Token
    └── whatsapp/            # Integração Z-API
```

## 🚀 Rodando Localmente

### Pré-requisitos
- Java 21+
- Docker e Docker Compose
- Maven 3.9+

### 1. Clonar e configurar
```bash
git clone <repo>
cd eventpass
cp .env.example .env
# Edite o .env com suas credenciais
```

### 2. Subir infraestrutura
```bash
docker-compose up -d mysql redis rabbitmq
```

### 3. Rodar a aplicação
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. Rodar tudo com Docker
```bash
docker-compose up --build
```

A API estará disponível em: `http://localhost:8080/api`

---

## 📡 Endpoints Principais

### 🔐 Autenticação
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/auth/cadastro/iniciar` | Inicia cadastro e envia OTP WhatsApp |
| POST | `/auth/login` | Login com e-mail e senha |

### 🗓️ Eventos
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/eventos` | Criar evento |
| GET | `/eventos` | Listar eventos do cliente |
| GET | `/eventos/{id}` | Buscar evento |
| PATCH | `/eventos/{id}/publicar` | Publicar |
| PATCH | `/eventos/{id}/iniciar` | Iniciar |
| PATCH | `/eventos/{id}/encerrar` | Encerrar |

### 👥 Convidados
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/eventos/{id}/convidados` | Cadastro manual |
| POST | `/eventos/{id}/convidados/importar` | Upload planilha Excel/CSV |
| POST | `/eventos/{id}/convidados/enviar-convites` | Disparo WhatsApp |
| GET | `/eventos/{id}/convidados/{cid}/qrcode` | Imagem PNG do QR Code |

### 📱 Dispositivos (Pareamento)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/dispositivos` | Cria device e retorna token de pareamento |
| POST | `/dispositivos/pareamento/concluir` | App Android finaliza o pareamento |
| PUT | `/dispositivos/{id}/evento/{eventoId}` | Associa device ao evento |
| DELETE | `/dispositivos/{id}` | Revoga acesso |

### 🚪 Portaria (App Android)
| Método | Rota | Descrição | Auth |
|--------|------|-----------|------|
| POST | `/portaria/leitura` | Processa leitura de QR Code | `X-Device-Token` |
| GET | `/portaria/buscar?eventoId=&nome=` | Busca manual por nome | `X-Device-Token` |

### 🌐 Convite Público (sem auth)
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/convites/{uuid}` | Dados do convite |
| GET | `/convites/{uuid}/qrcode.png` | QR Code em PNG |
| POST | `/convites/{uuid}/confirmar` | Confirmar presença |

### 📊 Dashboard ao vivo
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/eventos/{id}/dashboard` | Resumo em tempo real |
| GET | `/eventos/{id}/dashboard/entradas` | Lista de entradas |
| WS | `/ws` (STOMP) | WebSocket: `/topic/evento/{id}/entradas` |

---

## 🔑 Autenticação

### Usuário (Admin)
```
Authorization: Bearer <jwt_token>
```

### Dispositivo (App Portaria)
```
X-Device-Token: <device_token>
X-Device-Model: Samsung Galaxy A54     (opcional)
X-App-Version: 1.0.0                   (opcional)
```

---

## 📋 Planilha de Importação

Formatos aceitos: `.xlsx` e `.csv`

| Coluna | Obrigatório | Observação |
|--------|-------------|------------|
| `nome` | ✅ | Nome completo |
| `documento` | ❌ | CPF ou RG |
| `telefone` | ❌ | Com DDD, ex: +5511999999999 |
| `email` | ❌ | |
| `grupo_tag` | ❌ | Mesa, setor, turma |
| `acompanhantes` | ❌ | Número inteiro |

---

## 🌐 WebSocket — Dashboard ao vivo

```javascript
const socket = new SockJS('http://localhost:8080/api/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({ Authorization: 'Bearer ' + token }, () => {
  stompClient.subscribe('/topic/evento/1/entradas', (msg) => {
    const entrada = JSON.parse(msg.body);
    console.log(entrada); 
    // { convidadoNome, grupoTag, status, acompanhantes, horario }
  });
});
```

---

## 🐳 Variáveis de Ambiente

Veja `.env.example` para a lista completa.

---

## 🧪 Testes

```bash
mvn test
```

---

## 📦 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.2.5 | Framework principal |
| Spring Security | 6 | Auth JWT + Device Token |
| Spring WebSocket | - | Dashboard ao vivo (STOMP) |
| MySQL | 8.0 | Banco de dados principal |
| Redis | 7 | Cache e sessões |
| RabbitMQ | 3 | Fila de notificações |
| ZXing | 3.5 | Geração de QR Codes |
| Apache POI | 5.2 | Importação Excel |
| Lombok | - | Redução de boilerplate |
| MapStruct | 1.5 | Mapeamento DTO ↔ Entity |
