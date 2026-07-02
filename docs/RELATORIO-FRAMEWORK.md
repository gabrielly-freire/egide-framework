# Relatório — Framework de Ouvidoria (Core + Instância 1)

> Documento para explicar o trabalho na ótica de **framework**: o que é **fixo** (frozen spots),
> o que é **flexível** (hot spots) e **como** a instância personaliza sem modificar o Core.

---

## 1. A ideia central do framework

Um framework separa o que é **invariável** entre todas as aplicações do domínio (aqui: sistemas de
ouvidoria) do que **varia** de aplicação para aplicação.

- **Pontos fixos (frozen spots):** implementados **uma vez** no Core e reusados por todas as
  instâncias, sem alteração.
- **Pontos flexíveis (hot spots):** o Core define **o contrato** (o "buraco" a ser preenchido) e uma
  **implementação default segura**; cada instância **substitui** com a sua regra.

O princípio que rege isso é a **Inversão de Controle** ("don't call us, we'll call you"): a instância
não chama o Core; **o Core chama o código da instância** nos pontos flexíveis.

**Stack:** Java 21 · Spring Boot 4 · Spring Data JPA · MapStruct · Flyway · Maven multi-módulo.
O **Core é uma biblioteca** (`egide-core`, `packaging: jar`, sem `main`); a **Instância 1**
(`egide-instancia-1`) declara o Core como dependência.

---

## 2. Pontos FIXOS — implementados no Core (`br.imd.ufrn.core`)

Todos seguem o mesmo formato: **entidade + DTOs + mapper + repository + service + controller**.

| Ponto fixo | O que faz | Peça principal | Endpoint |
|---|---|---|---|
| **Registro de manifestações** | recepção, protocolo único (`YYYY-{10 chars}`), estado inicial `REGISTERED`, soft delete, timestamps | `Manifestation`, `ManifestationServiceImpl` | `/v1/manifestations` |
| **Avaliação de atendimento** | feedback do manifestante sobre o atendimento | `ServiceEvaluation` | `/v1/evaluations` |
| **Designação de responsável** | registro da atribuição do analista | `ResponsibleAssignment` | `/v1/assignments`, `/v1/designations` |
| **Registro de decisões e pareceres** | documentação formal (`DECISION`/`OPINION`) | `DecisionRecord` | `/v1/decisions` |
| **Histórico / auditoria** | rastreabilidade imutável das ações | `AuditEntry` | `/v1/audit` |
| **Geração de relatórios** | relatórios gerenciais/estatísticos | `ReportService` | `/v1/reports` |

> Estes seis são **idênticos** em qualquer instância. A Instância 1 os usa sem escrever uma linha:
> como a main class fica em `br.imd.ufrn`, o *component scan* cobre `br.imd.ufrn.core.*`
> automaticamente (controllers, services, repositories e entidades do Core sobem sozinhos).

---

## 3. Pontos FLEXÍVEIS — contrato + default no Core, personalização na Instância 1

### 3.1 O mecanismo (o coração do framework)

Para **cada** ponto flexível, o Core faz três coisas:

1. Define **o contrato** — uma `interface` (padrão **Strategy**) ou classe abstrata (padrão
   **Template Method**).
2. Fornece um **default seguro**.
3. Registra o default via **auto-configuração** com `@ConditionalOnMissingBean`:

```java
// br.imd.ufrn.core.config.CoreAutoConfiguration
@Bean
@ConditionalOnMissingBean(AnonymizationStrategy.class)   // "só crie o default SE a instância não tiver o seu"
public AnonymizationStrategy transparentAnonymizationStrategy() {
    return new TransparentAnonymizationStrategy();
}
```

A instância só precisa **declarar um bean** do tipo do contrato (um `@Component`). Aí o
`@ConditionalOnMissingBean` vê que já existe um bean daquele tipo e **desliga o default do Core** —
a personalização entra **sem tocar no Core**.

### 3.2 Os 5 pontos flexíveis e como a Instância 1 (Compliance) os preenche

| # | Ponto flexível | Contrato (Core) | Default (Core) | Override da Instância 1 | Padrão |
|---|---|---|---|---|---|
| 1 | **Anonimização** | `AnonymizationStrategy` | `TransparentAnonymizationStrategy` (não anonimiza) | `ComplianceAnonymizationStrategy` — chama o microserviço de IA (pseudonimização LGPD de título + descrição) | Strategy |
| 2 | **Categorização por IA** | `CategorizationStrategy` | `NoOpCategorizationStrategy` (não classifica) | `AiCategorizationStrategy` — chama a IA (`/analysis/analisar`), converte para os enums `ComplianceCategory`/`ComplianceRisk`; disparada **assíncrona** por evento | Strategy + Observer |
| 3 | **Conflito de interesse** | `ConflictOfInterestStrategy` | `NoConflictOfInterestStrategy` | `ComplianceConflictOfInterestStrategy` — regra por **hierarquia e departamento** (analista é o acusado, mesmo depto, ou acusado de cargo superior) | Strategy |
| 4 | **Designação (regra)** | `DesignationStrategy` | `ManualDesignationStrategy` (retorna `null`) | `ComplianceDesignationStrategy` — **sorteia** (`SecureRandom`) um ouvidor `LISTENER` excluindo conflitados | Strategy |
| 5 | **Workflow** | `WorkflowTemplate` (abstrata) | `DefaultWorkflowTemplate` | `ComplianceWorkflowTemplate` — `REGISTERED`(triagem,5d) → `IN_REVIEW`(investigação,30d) → `RESOLVED`; **sem recurso** | Template Method |

### 3.3 Como o Core "chama" cada ponto flexível (a Inversão de Controle na prática)

- **Anonimização:** `ManifestationService.create()` monta um `AnonymizationContext` (com os textos)
  e chama `strategy.anonymize(...)` — a instância decide *como* anonimizar.
- **Categorização:** `create()` publica um `ManifestationCreatedEvent`; a instância escuta de forma
  **assíncrona** (`@Async @TransactionalEventListener`) e chama `CategorizationService.categorize()`,
  que delega à `CategorizationStrategy`.
- **Conflito e Designação:** `DesignationService.autoAssign()` chama `DesignationStrategy.resolve()`
  para escolher o analista e `ConflictOfInterestStrategy.hasConflict()` para barrar impedidos.
- **Workflow:** `WorkflowService.advance()/appeal()` chama os métodos-template de `WorkflowTemplate`;
  a estrutura do algoritmo é fixa no Core, os passos variáveis são da instância.

---

## 4. A Instância 1 cresceu MUITO além do Core — sem modificá-lo

A Instância 1 trouxe um domínio inteiro que o **Core não conhece**, e mesmo assim se integrou:

- **Usuários/analistas, departamentos e segurança JWT** (`AppUser`, `Department`, `SecurityConfig`…).
  `AppUser` **reusa** a `BaseEntity` do Core e seu `id` **é** o `analystId` que os pontos fixos do
  Core referenciam por `Long` — zero acoplamento reverso.
- **Cliente do microserviço de IA** (RestClient) e o **modelo de acusação** (`ManifestationAccusation`)
  usado pela regra de conflito.

Isso é a prova de que o framework permite à instância **estender livremente** o que ele não previu.

---

## 5. Única mudança no Core compartilhado (decisão de design registrada)

Ao implementar o SLA, o Core recebeu um campo genérico **`Manifestation.deadlineAt`** (prazo da fase
atual). Decisão: **prazo é fixo (o campo mora no Core); a duração de cada fase é flexível**
(`WorkflowTemplate.deadlineFor`). É **retrocompatível** (nullable; default retorna `null`). Isso
seguiu o mesmo padrão já usado na categorização (o *resultado* de um ponto flexível — `category`,
`riskLevel` — é gravado como campo da `Manifestation`).

> ⚠️ Para colegas (instâncias 2/3): como o Hibernate roda em `ddl-auto: validate`, cada instância
> precisa de uma migração Flyway adicionando a coluna `deadline_at`.

---

## 6. Evidências

- **Testes:** Core **69** + Instância 1 **29** testes unitários (JUnit 5 + Mockito), todos verdes.
- **Verificação ao vivo** (app + PostgreSQL + microserviço de IA):
  - Anonimização real: `"diretor Carlos Pereira"` → `"gerente executivo Fernando Costa"` (título e descrição).
  - Categorização assíncrona real: `category=DENUNCIATION`, `riskLevel=CRITICAL`.
  - Conflito: ouvidor do mesmo depto do acusado é excluído; hierarquia (`GENERAL_LISTENER > MANAGER`) correta.
  - Designação: sorteia ouvidor elegível, pulando o conflitado.
  - Workflow + SLA: `REGISTERED` (+5d) → `IN_REVIEW` (+30d) → `RESOLVED` (sem prazo).
  - Segurança: registrar denúncia é público; gestão exige token JWT.

---

## 7. Conclusão para a defesa

- Os **6 pontos fixos** estão no Core e são reusados **sem alteração** pela Instância 1.
- Os **5 pontos flexíveis** têm **contrato + default** no Core e são **substituídos** pela Instância 1
  via `@ConditionalOnMissingBean`, usando **Strategy** e **Template Method**.
- A Instância 1 **nunca modifica o Core** — apenas o reutiliza e o estende.

O que ficou fora (entidades de fase do compliance, ações do Ouvidor Geral, anexos, PDF, notificações)
é **detalhe de aplicação** da Instância 1 — **não** adiciona pontos fixos nem flexíveis novos, e por
isso não é necessário para demonstrar o framework.
