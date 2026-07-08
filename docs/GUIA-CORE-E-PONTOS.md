# Guia — Core, Pontos Fixos e Flexíveis (Instância 1)

> Foco: **quem chama o quê, onde e como** — e, principalmente, **de onde vêm as variáveis de cada `Context`**
> que o Core entrega às estratégias da instância.

---

## 0. A regra de ouro (Inversão de Controle)

Você **nunca** instancia um `...Context`. **O Core** monta o context (a partir do request HTTP ou da
entidade no banco) e **chama** a sua estratégia. Sua instância só implementa *como* responder.

```
HTTP → Controller (Core) → Service (Core) → [monta o Context] → Strategy (SUA instância)
```

Onde o Core "desliga" o default e usa a sua implementação: `@ConditionalOnMissingBean` em
`CoreAutoConfiguration`. Você declara um `@Component` do tipo do contrato → o default some.

---

## 1. Pontos FIXOS (Core) — resumo

Implementados uma vez no Core (`br.imd.ufrn.core`), reusados sem alteração:

| Ponto fixo | Entidade | Service | Controller |
|---|---|---|---|
| Registro de manifestações | `Manifestation` | `ManifestationServiceImpl` | `/v1/manifestations` |
| Avaliação de atendimento | `ServiceEvaluation` | `ServiceEvaluationServiceImpl` | `/v1/evaluations` |
| Designação (registro) | `ResponsibleAssignment` | `ResponsibleAssignmentServiceImpl` | `/v1/assignments` |
| Decisões e pareceres | `DecisionRecord` | `DecisionRecordServiceImpl` | `/v1/decisions` |
| Auditoria | `AuditEntry` | `AuditEntryServiceImpl` | `/v1/audit` |
| Relatórios | — | `ReportServiceImpl` | `/v1/reports` |

O `Manifestation` também guarda o **resultado** dos pontos flexíveis: `category`, `riskLevel`
(categorização) e `deadlineAt` (prazo do workflow).

---

## 2. Pontos FLEXÍVEIS — o mecanismo

Para cada ponto: **contrato** (Core) + **default** (Core) + **override** (Instância 1).

```java
// br.imd.ufrn.core.config.CoreAutoConfiguration
@Bean
@ConditionalOnMissingBean(AnonymizationStrategy.class)   // só cria o default SE a instância não tiver o seu
public AnonymizationStrategy transparentAnonymizationStrategy() { return new TransparentAnonymizationStrategy(); }
```

---

## 3. Anonimização  — SÍNCRONA, dentro do `create`

| | |
|---|---|
| **Contrato** | `AnonymizationResult anonymize(AnonymizationContext context)` |
| **Context** | `AnonymizationContext(boolean anonymous, String type, String title, String description)` |
| **Default** | `TransparentAnonymizationStrategy` (devolve os textos inalterados) |
| **Override** | `ComplianceAnonymizationStrategy` (chama a IA) |
| **Quem chama** | `ManifestationServiceImpl.create()` e `.update()` |

**Onde e como o Core monta o context** (`ManifestationServiceImpl.create`):

```java
public ManifestationResponse create(ManifestationRequest request) {
    Manifestation entity = mapper.toEntity(request);
    entity.setProtocolNumber(...); entity.setStatus(REGISTERED); entity.setActive(true);

    // >>> monta o Context a partir do REQUEST HTTP e chama a estratégia <<<
    AnonymizationResult anonymized = anonymizationStrategy.anonymize(new AnonymizationContext(
            request.anonymous(), request.type(), request.title(), request.description()));
    entity.setTitle(anonymized.title());
    entity.setDescription(anonymized.description());
    ...
}
```

**De onde vem cada variável do Context:**

| Campo | Origem |
|---|---|
| `anonymous` | `request.anonymous()` — campo do JSON de `POST /v1/manifestations` |
| `type` | `request.type()` — idem |
| `title` | `request.title()` — idem |
| `description` | `request.description()` — idem |

> Observação-chave: roda **antes do `save()`**, então **não existe id ainda** — por isso a
> anonimização não recebe id. (Foi por isso que, ao chamar a IA, mandamos `report_id = 0`.)

---

## 4. Categorização (IA) — ASSÍNCRONA, via evento

| |                                                                                             |
|---|---------------------------------------------------------------------------------------------|
| **Contrato** | `CategorizationResult categorize(CategorizationContext context)`                            |
| **Context** | `CategorizationContext(Long manifestationId, String title, String description, String type)` |
| **Default** | `NoOpCategorizationStrategy` (devolve `null`/`null`)                                        |
| **Override** | `AiCategorizationStrategy` (chama `/analysis/analisar`)                                     |
| **Quem chama** | `CategorizationServiceImpl.categorize(Long id)`                                             |

**A cadeia de disparo (3 saltos):**

1. **Core publica o evento** (`ManifestationServiceImpl.create`, depois do save):
   ```java
   Manifestation saved = repository.save(entity);
   eventPublisher.publishEvent(new ManifestationCreatedEvent(saved.getId()));  // id REAL agora existe
   ```
2. **Sua instância escuta, de forma assíncrona** (`ManifestationCreatedListener`):
   ```java
   @Async("aiExecutor")
   @TransactionalEventListener(phase = AFTER_COMMIT)   // só depois do commit → manifestação já está no banco
   public void onManifestationCreated(ManifestationCreatedEvent event) {
       categorizationService.categorize(event.manifestationId());
   }
   ```
3. **O Core monta o Context a partir da ENTIDADE no banco** (`CategorizationServiceImpl.categorize`):
   ```java
   Manifestation m = repository.findById(manifestationId).orElseThrow(...);
   CategorizationResult result = categorizationStrategy.categorize(new CategorizationContext(
           m.getId(), m.getTitle(), m.getDescription(), m.getType()));   // <<< aqui
   m.setCategory(result.category());
   m.setRiskLevel(result.riskLevel());
   repository.save(m);
   ```

**De onde vem cada variável do Context:**

| Campo | Origem |
|---|---|
| `manifestationId` | `event.manifestationId()` → `m.getId()` (id REAL, já salvo) |
| `title` / `description` / `type` | da **entidade `Manifestation` carregada do banco** (não do request) |

> Diferença crucial vs. anonimização: aqui o context vem da **entidade persistida** (tem id), porque
> roda **depois** do save, em outra thread.

---

## 5. Conflito de interesse — SÍNCRONO, na designação/checagem

| | |
|---|---|
| **Contrato** | `boolean hasConflict(ConflictOfInterestContext context)` |
| **Context** | `ConflictOfInterestContext(Long manifestationId, Long analystId, String manifestationType)` |
| **Default** | `NoConflictOfInterestStrategy` (sempre `false`) |
| **Override** | `ComplianceConflictOfInterestStrategy` (hierarquia/departamento) |
| **Quem chama** | `DesignationServiceImpl` (em 2 lugares) |

**Lugar 1 — checagem direta** (`GET /v1/designations/{manifestationId}/conflict/{analystId}`):

```java
public boolean hasConflict(Long manifestationId, Long analystId) {
    Manifestation manifestation = manifestationRepository.findById(manifestationId).orElseThrow(...);
    ConflictOfInterestContext context = new ConflictOfInterestContext(
            manifestationId, analystId, manifestation.getType());   // <<<
    return conflictOfInterestStrategy.hasConflict(context);
}
```

**Lugar 2 — dentro do auto-assign** (ver seção 6).

**De onde vem cada variável do Context:**

| Campo | Origem (lugar 1) |
|---|---|
| `manifestationId` | **path da URL** `/conflict/{manifestationId}/...` |
| `analystId` | **path da URL** `.../{analystId}` |
| `manifestationType` | `manifestation.getType()` (entidade carregada) |

---

## 6. Designação — SÍNCRONA, no auto-assign

| | |
|---|---|
| **Contrato** | `Long resolve(DesignationContext context)` (id do analista, ou `null` = manual) |
| **Context** | `DesignationContext(Long manifestationId, String manifestationType)` |
| **Default** | `ManualDesignationStrategy` (retorna `null`) |
| **Override** | `ComplianceDesignationStrategy` (sorteia `LISTENER`, exclui conflitados) |
| **Quem chama** | `DesignationServiceImpl.autoAssign(Long id)` |

**Onde e como** (`POST /v1/designations/{manifestationId}/auto`):

```java
public ResponsibleAssignmentResponse autoAssign(Long manifestationId) {
    Manifestation manifestation = manifestationRepository.findById(manifestationId).orElseThrow(...);

    // (1) monta o context da DESIGNAÇÃO e resolve o analista
    DesignationContext designationContext = new DesignationContext(manifestationId, manifestation.getType());
    Long responsibleId = designationStrategy.resolve(designationContext);
    if (responsibleId == null) throw new AutoAssignmentUnavailableException(manifestationId);

    // (2) monta o context de CONFLITO com o analista escolhido e valida
    ConflictOfInterestContext conflictContext =
            new ConflictOfInterestContext(manifestationId, responsibleId, manifestation.getType());
    if (conflictOfInterestStrategy.hasConflict(conflictContext))
        throw new ConflictOfInterestException(responsibleId, manifestationId);

    return assignmentService.assign(new ResponsibleAssignmentRequest(manifestationId, responsibleId, null));
}
```

**De onde vem cada variável:**

| Context | Campo | Origem |
|---|---|---|
| `DesignationContext` | `manifestationId` | path da URL `/auto/{manifestationId}` |
| | `manifestationType` | `manifestation.getType()` (entidade) |
| `ConflictOfInterestContext` | `analystId` | **resultado de `designationStrategy.resolve(...)`** — a designação alimenta o conflito |

> É aqui que os dois pontos flexíveis se compõem: **designação escolhe → conflito valida**. Como a
> nossa `ComplianceDesignationStrategy` já exclui conflitados, o passo (2) do Core passa direto.

---

## 7. Workflow — SÍNCRONO, sem `Context` (recebe a entidade)

| | |
|---|---|
| **Contrato** | classe abstrata `WorkflowTemplate` (Template Method) |
| **Default** | `DefaultWorkflowTemplate` |
| **Override** | `ComplianceWorkflowTemplate` (triagem 5d → investigação 30d → decisão) |
| **Quem chama** | `WorkflowServiceImpl.advance/appeal` e `ManifestationServiceImpl.create` |

Diferente dos outros, o workflow **não usa um `Context`** — os métodos-template recebem a própria
`Manifestation` (ou o `ManifestationStatus`). O Core fixa o **algoritmo**; a instância só preenche os
passos variáveis:

```java
// WorkflowTemplate (Core) — algoritmo FIXO:
public final WorkflowStepResult advance(Manifestation manifestation) {
    if (isTerminal(manifestation.getStatus())) throw new WorkflowAdvanceNotAllowedException(...);
    ManifestationStatus next = resolveNextStatus(manifestation.getStatus());  // passo VARIÁVEL (instância)
    onBeforeAdvance(manifestation, next);                                     // hook opcional
    return new WorkflowStepResult(next, deadlineFor(next));                   // passo VARIÁVEL (instância)
}
```

```java
// ComplianceWorkflowTemplate (instância) — passos variáveis:
protected ManifestationStatus resolveNextStatus(ManifestationStatus current) {
    return switch (current) { case REGISTERED -> IN_REVIEW; case IN_REVIEW -> RESOLVED; ... };
}
protected Duration deadlineFor(ManifestationStatus status) {
    return switch (status) { case REGISTERED -> Duration.ofDays(5); case IN_REVIEW -> Duration.ofDays(30); ... };
}
```

**Quem chama, onde:**

| Método | Chamado por | Gatilho |
|---|---|---|
| `advance(m)` | `WorkflowServiceImpl.advance` | `POST /v1/workflow/{id}/advance` |
| `appeal(m)` | `WorkflowServiceImpl.appeal` | `POST /v1/workflow/{id}/appeal` |
| `initialDeadline()` | `ManifestationServiceImpl.create` | ao registrar (carimba o prazo da triagem) |

**Origem dos dados:** `manifestation.getStatus()` (entidade carregada). O `WorkflowService` converte o
`Duration` do `deadlineFor` em instante absoluto: `deadlineAt = now + Duration`.

---

## 8. Tabela-resumo: origem dos Context (o que a banca vai perguntar)

| Ponto flexível | Context | Vem de | Momento | Síncrono? |
|---|---|---|---|---|
| Anonimização | `AnonymizationContext` | **request HTTP** | dentro do `create` (antes do save) | ✅ síncrono |
| Categorização | `CategorizationContext` | **entidade no banco** | após commit, via evento | ❌ assíncrono |
| Conflito | `ConflictOfInterestContext` | **path da URL** + entidade | na checagem / auto-assign | ✅ síncrono |
| Designação | `DesignationContext` | **path da URL** + entidade | no auto-assign | ✅ síncrono |
| Workflow | *(sem context — a `Manifestation`)* | **entidade no banco** | advance/appeal/create | ✅ síncrono |

**Frase para a defesa:** *"O context nunca é criado pela instância — o Core o monta a partir do request
HTTP (anonimização), da entidade persistida (categorização, workflow) ou dos parâmetros da URL
(conflito, designação), e então chama a minha estratégia. Isso é Inversão de Controle."*
