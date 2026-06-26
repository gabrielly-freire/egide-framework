# Framework de Ouvidoria

## Visão geral

Framework modular para construção de sistemas de ouvidoria. A ideia central é separar o que é **fixo** (Core) do que é **variável** (Instâncias). Instâncias não modificam o Core — apenas o reutilizam e personalizam os pontos variáveis.

**Stack:** Java 21 · Spring Boot 4.0.4 · Spring Data JPA · MapStruct · Lombok · PostgreSQL · Flyway · Maven multi-módulo

---

## Estrutura do projeto

```
projeto/
├── pom.xml                     ← pai agregador (versões centralizadas)
├── core/                       ← biblioteca reutilizável, sem main class
├── backend/
│   ├── instancia-1/            ← Sistema de Compliance Corporativo
│   ├── instancia-2/            ← Ouvidoria Universitária
│   └── instancia-3/            ← Atendimento Público
└── frontend/
    ├── instancia-1/
    ├── instancia-2/
    └── instancia-3/
```

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

Configuráveis por instância. O Core **não implementa** nenhum desses comportamentos — define apenas as interfaces ou abstrações que cada instância concretiza.

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

Empresas privadas · denúncias internas · `backend/instancia-1/` · `br.imd.ufrn.egide`

| Ponto variável | Configuração |
|---|---|
| Anonimização | Mascaramento estrito para denúncias anônimas |
| Categorização | Assédio, fraude, corrupção, conduta antiética |
| Conflito de interesse | Baseado em hierarquia e cargos |
| Workflow | Investigação sigilosa, sem opção de recurso simples |

Implementação atual: autenticação JWT, workflow de 5 fases, categorização por IA (microserviço externo), exportação PDF (OpenPDF).
PostgreSQL local: porta 5434, banco `egidedb`.

---

### Instância 2 — Ouvidoria Universitária

Universidades · alunos, professores e servidores · `backend/instancia-2/`

| Ponto variável | Configuração |
|---|---|
| Anonimização | Parcial — identidade visível apenas para a Ouvidoria Geral |
| Categorização | Infraestrutura, matrícula, atendimento etc. |
| Conflito de interesse | Analista não pode pertencer ao mesmo centro/departamento do denunciado |
| Workflow | Mediação interna; prazo de recurso atrelado ao calendário acadêmico |

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

### Registro de Manifestações

**O Core faz:**
- Cadastrar, consultar, atualizar e listar manifestações
- Gerar número de protocolo único (formato `YYYY-{10 chars UUID}`)
- Manter estado inicial (`REGISTERED`)
- Exclusão lógica (`active = false`)
- Controlar `createdAt` / `updatedAt` via `@PrePersist` / `@PreUpdate`

**O Core não faz:**
- Transições de status além de `REGISTERED` (workflow é ponto variável)
- Anonimização, categorização por IA, designação de responsável (pontos variáveis)

**Estrutura de pacotes:**
```
core/src/main/java/br/imd/ufrn/egide/core/
├── shared/
│   ├── domain/BaseEntity.java           ← id, createdAt, updatedAt, active
│   └── exception/CoreException.java     ← base abstrata para exceções do Core
└── manifestation/
    ├── domain/
    │   ├── Manifestation.java
    │   └── ManifestationStatus.java     ← REGISTERED, IN_REVIEW, RESOLVED, CLOSED
    ├── application/
    │   ├── dto/                         ← CreateManifestationRequest, UpdateManifestationRequest, ManifestationResponse
    │   ├── mapper/ManifestationMapper.java
    │   └── service/
    │       ├── ManifestationService.java        ← interface
    │       └── ManifestationServiceImpl.java
    ├── exception/
    │   ├── ManifestationNotFoundException.java
    │   └── DuplicateProtocolException.java
    └── infrastructure/
        ├── persistence/ManifestationRepository.java
        └── web/
            ├── ManifestationController.java     ← /v1/manifestations
            └── GlobalExceptionHandler.java
```

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
- **Pontos variáveis não têm implementação no Core:** o Core pode definir interfaces ou abstrações, mas nunca implementações concretas de comportamentos variáveis.
