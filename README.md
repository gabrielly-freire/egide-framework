# Framework ouvidoria

Framework modular para sistemas de ouvidoria, composto por pontos fixos (comportamentos obrigatórios em todas as instâncias) e pontos variáveis (comportamentos configuráveis por instância).

## Estrutura do repositório

| Módulo                            | Caminho                                                        | Descrição |
|-----------------------------------|----------------------------------------------------------------|---|
| Core                              | [`backend/core`](backend/core)                                 | Biblioteca reutilizável com os pontos fixos e os contratos/defaults dos pontos variáveis |
| Instância 1 — Backend             | [`backend/instancia-1`](backend/instancia-1)                   | Sistema de Compliance Corporativo, construído sobre o Core |
| Instância 1 — Backend (legado)    | [`backend/instancia-1-original`](backend/instancia-1-original) | Monolito de referência, anterior à extração do framework |
| Instância 1 — Frontend            | [`frontend/instancia1`](frontend/instancia1)                   | Aplicação Angular (Egide) |
| Instância 1 — Microsserviço de IA | [`egide-ia-ms`](egide-ia-ms)                                   | FastAPI + LangGraph, anonimização/pseudonimização inteligente (LGPD) |
| Instância 2 — Backend             | [`backend/instancia-2`](backend/instancia-2)                   | Ouvidoria Universitária, construído sobre o Core |
| Instância 2 — Frontend            | [`frontend/instancia-2`](frontend/instancia-2)                 | Aplicação Angular |
| Instância 3 — Backend             | [`backend/instancia-3`](backend/instancia-3)                   | Sistema de Atendimento Público (LAI), construído sobre o Core |
| Instância 3 — Frontend            | [`frontend/instancia-3`](frontend/instancia-3)                 | Aplicação Angular |

## Core

[Backend](backend/core)

### Pontos fixos

Presentes em todas as instâncias do framework, independentemente do contexto de uso:

- **Registro de manifestações** — recepção e armazenamento de toda manifestação recebida.
- **Avaliação de atendimento** — mecanismo de feedback sobre a qualidade do atendimento prestado.
- **Designação de responsável pela análise** — atribuição de um analista responsável por cada manifestação.
- **Registro de decisões e pareceres** — documentação formal das conclusões e encaminhamentos.
- **Histórico/auditoria das ações das manifestações** — rastreabilidade completa de todas as ações realizadas.
- **Geração de relatórios** — produção de relatórios gerenciais e estatísticos.

### Pontos variáveis

Configuráveis por instância, adaptando o framework ao contexto de cada organização:

- **Regras de anonimização** — define o grau de sigilo da identidade do manifestante.
- **Modelo de triagem e categorização por IA** — critérios e categorias usados para classificação automática das manifestações.
- **Critérios de conflito de interesse** — condições que impedem um analista de atuar em determinada manifestação.
- **Regras de designação do responsável** — lógica de atribuição do analista responsável.
- **Etapas do workflow** — sequência de fases, prazos e possibilidades de recurso do processo.

## Instâncias

### Instância 1 — Sistema de compliance corporativo

Usado por empresas privadas para receber denúncias internas.

[Backend](backend/instancia-1) | [Frontend](frontend/instancia1) | [Backend legado](backend/instancia-1-original) | [Microsserviço de IA](egide-ia-ms)

| Ponto Variável         | Configuração                                                                                        |
|------------------------|-----------------------------------------------------------------------------------------------------|
| Anonimização           | Mascaramento estrito de dados para denúncias anônimas.                                              |
| Modelo e categorização | Denúncias de assédio, fraude, corrupção ou conduta antiética.                                       |
| Conflito de interesse  | Validação baseada na hierarquia e cargos.                                                           |
| Workflow               | Fluxo restrito com etapa de investigação sigilosa e sem opção de recurso simples pelo manifestante. |


### Instância 2 — Sistema de ouvidoria universitária

Usado por universidades para receber manifestações de alunos, professores e servidores.

[Backend](backend/instancia-2) | [Frontend](frontend/instancia2)

| Ponto Variável         | Configuração                                                                                   |
|------------------------|------------------------------------------------------------------------------------------------|
| Anonimização           | Parcial — identidade visível apenas para a Ouvidoria Geral da instituição.                     |
| Modelo e categorização | Triagem automática em categorias institucionais (infraestrutura, matrícula, atendimento etc.). |
| Conflito de interesse  | Analista não pode pertencer ao mesmo centro acadêmico ou departamento do denunciado.           |
| Workflow               | Etapa de mediação interna e prazo de recurso atrelado ao calendário acadêmico.                 |


### Instância 3 — Sistema de atendimento público

Usado por órgãos públicos para manifestações de cidadãos (ex.: Ouvidoria Geral do SUS).

[Backend](backend/instancia-3) | [Frontend](frontend/instancia-3)

| Ponto Variável         | Configuração                                                                                                           |
|------------------------|------------------------------------------------------------------------------------------------------------------------|
| Modelo e categorização | Classificação automática por tipo de manifestação e órgão responsável.                                                 |
| Conflito de interesse  | Impedimento legal quando o analista for citado ou tiver parentesco com os envolvidos.                                  |
| Designação             | Direcionamento automático por especialidade do órgão ou região de saúde afetada.                                       |
| Workflow               | Workflow rígido baseado na Lei de Acesso à Informação (LAI), com direito a recurso em três instâncias administrativas. |

## Equipe

- Francisca Gabrielly Lopes Freire [(gabrielly-freire)](https://github.com/gabrielly-freire)
- Gabriel Ribeiro Barbosa da Silva [(gabriel-ribeiro-099)](https://github.com/gabriel-ribeiro-099)
- Nicole Carvalho Nogueira [(nicolecnogueira)](https://github.com/nicolecnogueira)