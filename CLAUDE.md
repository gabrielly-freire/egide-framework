# Framework de Ouvidoria

## Visão geral

Framework modular para construção de sistemas de ouvidoria. A ideia central é separar o que é **fixo** (Core) do que é **variável** (Instâncias). Instâncias não modificam o Core — apenas o reutilizam e personalizam os pontos variáveis.

**Stack:** Java 21 · Spring Boot 4.0.4 · Spring Data JPA · MapStruct · Lombok · PostgreSQL · Flyway · Maven multi-módulo

---

## Estrutura do projeto

```
projeto/
├── pom.xml                     ← pai agregador (versões centralizadas)
├── backend/
│   ├── core/                   ← biblioteca reutilizável, sem main class (pacote br.imd.ufrn.core)
│   ├── instancia-1/            ← Sistema de Compliance Corporativo (pacote br.imd.ufrn)
│   ├── instancia-2/            ← Ouvidoria Universitária (pacote br.imd.ufrn)
│   └── instancia-1-original/   ← monolito legado de referência (pré-framework)
└── frontend/
    └── instancia1/
```

> A instância 3 está especificada neste documento, mas ainda não foi criada como módulo Maven. O `pom.xml` raiz agrega hoje `backend/core`, `backend/instancia-1` e `backend/instancia-2`.

O projeto é um **Maven multi-módulo**. O `pom.xml` raiz é o pai agregador: centraliza versões do Java, Spring Boot, MapStruct e outras dependências comuns. As instâncias declaram o Core como dependência sem precisar repetir versões.

---

## Pontos fixos (Core)

Presentes em **todas** as instâncias, sem variação. O Core os implementa completamente.

| Ponto fixo | Responsabilidade |
|---|---|
| **Registro de manifestações** | Recepção, armazenamento, protocolo único, estado inicial e exclusão lógica |
| **Avaliação de atendimento** | Mecanismo de feedback sobre a qualidade do atendimento prestado |
| **Designação de responsável** | Atribuição de um analista responsável por cada manifestação |
| **Registro de decisões e pareceres** | Documentação formal das conclusões e encaminhamentos |
| **Histórico / auditoria** | Rastreabilidade completa de todas as ações realizadas sobre uma manifestação |
| **Geração de relatórios** | Produção de relatórios gerenciais e estatísticos |

---

## Pontos variáveis (Instâncias)

Configuráveis por instância. O Core define o **contrato** de cada ponto (interface ou classe abstrata) e fornece uma **implementação default segura**, registrada por auto-configuração com `@ConditionalOnMissingBean`. Quando a instância declara seu próprio bean, o default é automaticamente desligado e substituído — a instância personaliza sem tocar no Core.

| Ponto variável | O que varia |
|---|---|
| **Regras de anonimização** | Grau de sigilo da identidade do manifestante |
| **Modelo de triagem e categorização por IA** | Critérios e categorias usados para classificação automática |
| **Critérios de conflito de interesse** | Condições que impedem um analista de atuar em determinada manifestação |
| **Regras de designação do responsável** | Lógica de atribuição do analista responsável |
| **Etapas do workflow** | Sequência de fases, prazos e possibilidades de recurso |

---

## Instâncias

### Instância 1 — Sistema de Compliance Corporativo

Empresas privadas · denúncias internas · `backend/instancia-1/` · pacote `br.imd.ufrn` (main class `EgideApplication`)

| Ponto variável | Configuração |
|---|---|
| Anonimização | Mascaramento estrito para denúncias anônimas |
| Categorização | Assédio, fraude, corrupção, conduta antiética |
| Conflito de interesse | Baseado em hierarquia e cargos |
| Workflow | Investigação sigilosa, sem opção de recurso simples |

Alvo dos pontos variáveis (do monolito legado `instancia-1-original`, a serem reimplementados sobre o Core): autenticação JWT, workflow de 5 fases, categorização por IA (microserviço externo), exportação PDF (OpenPDF).

Estado atual de `backend/instancia-1`: esqueleto (main class + `application.yml` + `pom.xml`). Ainda não sobrepõe nenhum ponto variável, então roda com os defaults do Core. Migração de schema via Flyway (`db/migration/V1`); Hibernate em `ddl-auto: validate`.
PostgreSQL local: porta 5434, banco `egidedb`.

---

### Instância 2 — Ouvidoria Universitária

Universidades · alunos, professores e servidores · `backend/instancia-2/` · pacote `br.imd.ufrn` (main class `OuvidoriaUniversitariaApplication`)

| Ponto variável | Configuração | Bean da instância (desliga o default do Core) |
|---|---|---|
| Anonimização | Parcial — mascara identificadores diretos (CPF, matrícula, e-mail, telefone); nome do manifestante permanece visível | `PartialAnonymizationStrategy` |
| Categorização | Heurística de palavras-chave em categorias institucionais: `INFRAESTRUTURA`, `MATRICULA`, `ATENDIMENTO`, `ACADEMICO`, `FINANCEIRO`, `OUTROS`; `riskLevel` sempre `null`. Disparada por listener síncrono do `ManifestationCreatedEvent` | `UniversityCategorizationStrategy` |
| Conflito de interesse | Impedimento quando analista e denunciado são da mesma unidade acadêmica | `SameUnitConflictOfInterestStrategy` |
| Designação | Mantém o default manual do Core (não é ponto customizado nesta instância) | — |
| Workflow | Mediação interna (`IN_REVIEW`); recurso permitido em `RESOLVED` reabre a mediação; prazos de mediação (15d) e recurso (30d) | `UniversityWorkflowTemplate` |

Dados próprios da instância (não existem no Core), para o conflito de interesse:
- `AcademicMember` (nome + unidade acadêmica) e `ManifestationAccusation` (vincula manifestação → denunciado), com migração Flyway `db/migration/V2`.
- Endpoints de apoio: `POST/GET /v1/academic-members` e `POST/GET /v1/manifestations/{id}/accusation`.

PostgreSQL local: porta 5434, banco `egide_universitaria`; app na porta 8081.

---

### Instância 3 — Atendimento Público

Órgãos públicos · cidadãos · ex.: Ouvidoria Geral do SUS · `backend/instancia-3/`

| Ponto variável | Configuração |
|---|---|
| Categorização | Por tipo de manifestação e órgão responsável |
| Conflito de interesse | Impedimento legal (citação ou parentesco) |
| Designação | Automática por especialidade do órgão ou região afetada |
| Workflow | Baseado na LAI, recurso em três instâncias administrativas |

---

## Core — Módulos implementados

O Core (`backend/core`, pacote `br.imd.ufrn.core`) já implementa **todos os pontos fixos** e expõe os **contratos + defaults dos pontos variáveis**. Cada ponto fixo segue o mesmo formato: entidade + DTOs + mapper + repository + service (interface/impl) + controller.

**Pontos fixos implementados:**
- **Registro de manifestações** — protocolo único (`YYYY-{10 chars UUID}`), estado inicial `REGISTERED`, soft delete, timestamps via `@PrePersist`/`@PreUpdate`.
- **Avaliação de atendimento** — `ServiceEvaluation` (uma por manifestação).
- **Designação de responsável** — `ResponsibleAssignment` (atribuição manual via `POST /v1/assignments`).
- **Registro de decisões e pareceres** — `DecisionRecord` (`DECISION` / `OPINION`).
- **Histórico / auditoria** — `AuditEntry` (imutável, sem soft delete).
- **Geração de relatórios** — `ReportService` (`ManifestationSummaryReport`).

**Pontos variáveis (contrato + default no Core):**
- `AnonymizationStrategy` → default `TransparentAnonymizationStrategy` (não anonimiza)
- `ConflictOfInterestStrategy` → default `NoConflictOfInterestStrategy`
- `DesignationStrategy` → default `ManualDesignationStrategy` (retorna `null` = designação manual)
- `WorkflowTemplate` (abstrata, Template Method) → default `DefaultWorkflowTemplate`
- `CategorizationStrategy` → default `NoOpCategorizationStrategy` (não classifica); resultado (`category`/`riskLevel`, ambos `String` nullable) gravado na `Manifestation` pelo `CategorizationService`. Gancho: `create()` publica `ManifestationCreatedEvent`; a instância escuta de forma assíncrona para disparar a IA.

Defaults registrados em `config/CoreAutoConfiguration` (`@AutoConfiguration`), listada em
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

**Estrutura de pacotes (layout flat por camada):**
```
backend/core/src/main/java/br/imd/ufrn/core/
├── config/          ← CoreAutoConfiguration
├── domain/          ← BaseEntity, Manifestation(+Status), AuditEntry, DecisionRecord(+Type),
│                       ResponsibleAssignment, ServiceEvaluation
├── dto/             ← *Request / *Response (records) + ManifestationSummaryReport
├── mapper/          ← mappers MapStruct
├── persistence/     ← repositories Spring Data
├── service/         ← <X>Service + <X>ServiceImpl (inclui WorkflowService, ReportService)
├── web/             ← controllers + GlobalExceptionHandler
├── exception/       ← CoreException + exceções específicas
├── anonymization/   ← AnonymizationStrategy + context + default  (ponto variável)
├── conflict/        ← ConflictOfInterestStrategy + context + default
├── designation/     ← DesignationStrategy + context + default
├── categorization/  ← CategorizationStrategy + context + result + NoOp default
├── event/           ← ManifestationCreatedEvent (gancho p/ a instância disparar a IA)
└── workflow/        ← WorkflowTemplate (abstrata) + WorkflowStepResult + DefaultWorkflowTemplate
```

> Como `EgideApplication` fica em `br.imd.ufrn`, o component scan da instância cobre `br.imd.ufrn.core.*` automaticamente: controllers, services, repositories e entidades do Core são carregados sem configuração extra; só os beans de estratégia vêm da auto-configuração.

**Endpoints:**
| Método | Path | Status |
|---|---|---|
| POST | `/v1/manifestations` | 201 |
| GET | `/v1/manifestations/{id}` | 200 |
| GET | `/v1/manifestations/protocol/{protocolNumber}` | 200 |
| GET | `/v1/manifestations?page&size&sort&direction` | 200 |
| PUT | `/v1/manifestations/{id}` | 200 |
| DELETE | `/v1/manifestations/{id}` | 204 |

---

## Convenções de código

- **Injeção por construtor** via `@RequiredArgsConstructor` (Lombok) com campos `final`
- **Service:** interface + implementação separadas (`XService` / `XServiceImpl`)
- **Mapper:** MapStruct com `componentModel = "spring"`; update usa `@BeanMapping(nullValuePropertyMappingStrategy = IGNORE)` para comportamento PATCH
- **Soft delete:** entidades herdam `active: Boolean` de `BaseEntity`; remoção seta `active = false` e salva; `@SQLRestriction("active = true")` filtra automaticamente nas queries JPA
- **DTOs:** records Java para imutabilidade
- **Exceções:** herdam de `CoreException extends RuntimeException`; tratadas por `@RestControllerAdvice` retornando `ProblemDetail` (RFC 9457)
- **Transações:** `@Transactional` na classe de serviço; leituras com `@Transactional(readOnly = true)`
- **Testes unitários:** JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`), sem contexto Spring

---

## Decisões arquiteturais

- **Multi-módulo Maven** (não repositórios separados): instâncias são co-desenvolvidas pela mesma equipe sem ciclos de deploy independentes.
- **Core é biblioteca** (`<packaging>jar</packaging>`), sem `spring-boot-maven-plugin` nem `main class`.
- **Status de workflow fora do Core:** o Core define o enum `ManifestationStatus` com estados universais, mas não implementa transições — responsabilidade do workflow de cada instância.
- **Pontos variáveis: contrato + default no Core, personalização na instância:** o Core define a interface/abstração de cada ponto variável (`AnonymizationStrategy`, `ConflictOfInterestStrategy`, `DesignationStrategy`, `WorkflowTemplate`, `CategorizationStrategy`) e registra uma implementação default segura via `CoreAutoConfiguration` (`@ConditionalOnMissingBean`). A instância sobrepõe declarando seu próprio bean — sem modificar o Core. Padrões usados: **Strategy** (anonimização, conflito, designação) e **Template Method** (workflow).
