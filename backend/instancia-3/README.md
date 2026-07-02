# Instância 3 — Sistema de Atendimento Público (Ouvidoria Pública / LAI)

Instância do framework voltada a **órgãos públicos** (ex.: Ouvidoria Geral do SUS), com regras
baseadas na **Lei de Acesso à Informação (LAI, Lei 12.527/2011)**. Reaproveita os pontos fixos do
Core sem alteração e personaliza os pontos variáveis descritos abaixo.

- **Módulo:** `backend/instancia-3` (pacote `br.imd.ufrn.atendimento`, main class `Application`)
- **Porta:** `8083` · **Banco:** Postgres local `5435`/`atendimentodb` (`docker-compose.yaml`)
- **Frontend:** `frontend/instancia-3` (Angular, `ng serve`, proxy `/api` → `8083`)

---

## Pontos fixos (herdados do Core, sem override)

Implementados inteiramente pelo Core — a instância só os consome via component scan
(`br.imd.ufrn.core.*`), sem nenhuma classe própria para eles.

| Ponto fixo | Endpoint(s) | Observação |
|---|---|---|
| Registro de manifestações | `POST/GET/PUT/DELETE /v1/manifestations` | Protocolo único `YYYY-{10 chars}`, soft delete, estado inicial `REGISTERED` |
| Avaliação de atendimento | `POST/GET/DELETE /v1/evaluations` | Uma avaliação por manifestação |
| Designação de responsável (endpoint) | `POST /v1/assignments`, `POST /v1/designations/{id}/auto` | O *mecanismo* é fixo; a *regra* de quem é escolhido automaticamente é variável (ver abaixo) |
| Registro de decisões e pareceres | `POST/GET/DELETE /v1/decisions` | Tipos `DECISION` / `OPINION` |
| Histórico / auditoria | `POST/GET /v1/audit` | Imutável, sem soft delete |
| Geração de relatórios | `GET /v1/reports/summary` | Estatísticas agregadas de manifestações |

---

## Pontos variáveis (personalizados nesta instância)

### 1. Categorização — `categorization/PublicServiceCategorizationStrategy`

**Regra:** a categoria da manifestação é o próprio `type` informado pelo cidadão (ex.: `SAUDE`,
`EDUCACAO`, `INFRAESTRUTURA`). Não há conceito de "risco" — a LAI não prevê triagem por risco como
o Compliance da instância 1 prevê.

```java
categorize(context) -> new CategorizationResult(context.type(), /* riskLevel */ null)
```

Essa categoria é a mesma chave usada pela designação automática (ver item 3), então o tipo
escolhido pelo cidadão já direciona a manifestação ao órgão especialista correto.

### 2. Conflito de interesse — `conflict/LegalConflictStrategy`

**Regra:** impedimento legal (não hierárquico, como na instância 1). Um analista está impedido de
atuar numa manifestação se existir um registro de `LegalImpediment` vinculando os dois, com motivo:

| `ImpedimentReason` | Significado |
|---|---|
| `CITATION` | Citação — o analista foi citado na própria manifestação |
| `KINSHIP` | Parentesco com o manifestante ou com a parte envolvida |

CRUD próprio em `POST/GET/DELETE /v1/legal-impediments` (`LegalImpedimentController`). A checagem
roda automaticamente dentro de `POST /v1/designations/{id}/auto` (o Core lança
`ConflictOfInterestException` se o analista escolhido tiver impedimento).

### 3. Designação automática — `designation/RegionDesignationStrategy`

**Regra (LAI):** designação automática por **especialidade do órgão OU região afetada**.

1. Tenta achar um analista cuja `specialty` seja igual ao `type` da manifestação
   (`AnalystRepository.findFirstBySpecialty`).
2. Se não encontrar, cai para a `affectedRegion` declarada na manifestação
   (`AnalystRepository.findFirstByRegion`).
3. Se nenhum dos dois encontrar analista, retorna `null` → designação manual via
   `POST /v1/assignments`.

`affectedRegion` é um campo opcional em `ManifestationRequest`/`ManifestationResponse` (Core),
preenchido pelo cidadão no formulário de abertura da manifestação (campo "Região afetada" no
portal). Sem essa informação, só o passo 1 (especialidade) é possível.

> **O que mudou:** antes desta rodada, a estratégia só considerava especialidade — o nome da
> classe (`RegionDesignationStrategy`) já sugeria o uso de região, mas o campo nem existia no
> Core. Foi adicionado `affectedRegion` a `Manifestation`/`DesignationContext` e o fallback por
> região na estratégia (`V3__add_affected_region_to_manifestations.sql`).

### 4. Workflow — `workflow/PublicServiceWorkflowTemplate`

**Regra (LAI):** fluxo linear com prazo fixo e recurso limitado a três instâncias administrativas.

```
REGISTERED --advance--> IN_REVIEW --advance--> RESOLVED --advance--> CLOSED
```

- **Prazo:** 20 dias fixos para qualquer fase (`deadlineFor` sempre retorna `Duration.ofDays(20)`).
- **Recurso (`appeal`):** permitido enquanto o status for `IN_REVIEW` ou `RESOLVED` **e** o número
  de recursos já interpostos (`Manifestation.appealCount`) for menor que 3. Cada recurso aceito
  volta o status para `IN_REVIEW` e incrementa o contador. No 4º recurso, o Core lança
  `WorkflowAppealNotAllowedException` (`422`).

> **O que mudou:** antes, `isAppealAllowed` recebia só o `ManifestationStatus` e permitia recurso
> indefinidamente em `IN_REVIEW`/`RESOLVED`, sem limite. Para cobrir "recurso em três instâncias
> administrativas" da LAI, o Core passou a expor a `Manifestation` inteira ao método (em vez do
> status isolado), adicionou o campo `appealCount` (default `0`,
> `V4__add_appeal_count_to_manifestations.sql`) e passou a incrementá-lo dentro de
> `WorkflowTemplate.appeal()`. A instância usa esse contador para aplicar o limite de 3; o
> `DefaultWorkflowTemplate` do Core (usado por instâncias sem override) segue sempre bloqueando
> recurso, então não foi afetado na prática.

> Esta instância não sobrepõe `AnonymizationStrategy` — usa o default do Core
> (`TransparentAnonymizationStrategy`), que devolve o texto sem qualquer alteração.

---

## Módulos próprios da instância (fora do catálogo de pontos variáveis)

Necessários para operar a instância, mas não são "pontos variáveis" do framework — são
funcionalidades específicas deste domínio (analistas com login, autenticação):

- **Analistas** (`domain/Analyst`, `AnalystRole`): CRUD em `/v1/analysts`, com `specialty` e
  `region` usados pelas estratégias de designação/conflito acima.
- **Autenticação JWT** (`service/JwtService`, `AnalystUserDetailsService`,
  `config/JwtAuthenticationFilter`): login em `POST /v1/auth/login`, token Bearer stateless.
  `GET /v1/auth/me` retorna o analista autenticado.
- **Segurança** (`config/SecurityConfig`): `/v1/auth/login`, `POST /v1/manifestations` e
  `GET /v1/manifestations/protocol/**` são públicos (cidadão não precisa de login para abrir ou
  consultar manifestação); todo o resto exige token. `/error` também é público, para que erros
  reais (400, 500) não sejam mascarados como 401 pelo handler de autenticação.

---

## Migrations (Flyway)

| Arquivo | Conteúdo |
|---|---|
| `V1__create_schema.sql` | Schema completo (tabelas do Core + `analysts` + `legal_impediments`), já no estado final |
| `V2__seed_data.sql` | Seed de analistas (senha `password` para todos, incluindo `admin@sus.gov.br`) |
| `V3__add_affected_region_to_manifestations.sql` | Coluna `affected_region`, usada pela designação por região |
| `V4__add_appeal_count_to_manifestations.sql` | Coluna `appeal_count`, usada pelo limite de 3 recursos |

`V1`/`V2` foram consolidados a partir de 6 migrations incrementais originais (drift de schema e
hash de senha corrigidos ao longo do caminho) para manter o histórico limpo em ambiente de
desenvolvimento local.

---

## Autenticação — usuários seed

Todos com senha **`password`**:

| Email | Role | Especialidade | Região |
|---|---|---|---|
| ana.oliveira@sus.gov.br | ANALYST | SAUDE | Norte |
| carlos.santos@sus.gov.br | ANALYST | EDUCACAO | Nordeste |
| fernanda.lima@sus.gov.br | ANALYST | INFRAESTRUTURA | Centro-Oeste |
| marcos.vieira@sus.gov.br | ANALYST | ASSISTENCIA_SOCIAL | Sudeste |
| juliana.costa@sus.gov.br | ANALYST | MEIO_AMBIENTE | Sul |
| admin@sus.gov.br | ADMIN | GESTAO | Nacional |
