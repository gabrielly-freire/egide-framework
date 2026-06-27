# Etapas do Workflow — Padrão Template Method

## Visão geral

O workflow é um **ponto variável**: cada instância define sua própria sequência de fases, prazos e possibilidades de recurso. O Core define a estrutura do algoritmo e as regras fixas — as instâncias preenchem os passos variáveis via subclasse.

---

## Participantes do padrão

| Papel (GoF) | Classe | Módulo |
|---|---|---|
| **AbstractClass** | `WorkflowTemplate` | Core |
| **ConcreteClass (padrão)** | `DefaultWorkflowTemplate` | Core |
| **ConcreteClass (instância-1)** | `ComplianceWorkflowTemplate` | instancia-1 |
| **ConcreteClass (instância-3)** | `PublicServiceWorkflowTemplate` | instancia-3 |
| **Context** | `WorkflowServiceImpl` | Core |
| **Result** | `WorkflowStepResult` | Core |

---

## Estrutura de `WorkflowTemplate`

```java
public abstract class WorkflowTemplate {

    // Template methods — estrutura fixa, não sobreponíveis
    public final WorkflowStepResult advance(Manifestation manifestation) { ... }
    public final WorkflowStepResult appeal(Manifestation manifestation)  { ... }

    // Passo fixo auxiliar
    protected final boolean isTerminal(ManifestationStatus status) { ... }

    // Passos variáveis — obrigatórios na subclasse
    protected abstract ManifestationStatus resolveNextStatus(ManifestationStatus current);
    protected abstract ManifestationStatus resolveAppealStatus(ManifestationStatus current);
    protected abstract boolean isAppealAllowed(ManifestationStatus status);
    protected abstract Duration deadlineFor(ManifestationStatus status);

    // Hooks — opcionais (ex.: auditoria, notificações)
    protected void onBeforeAdvance(Manifestation manifestation, ManifestationStatus next) {}
    protected void onBeforeAppeal(Manifestation manifestation, ManifestationStatus appealStatus) {}
}
```

### Algoritmo fixo de `advance()`

```
1. Se status é terminal (RESOLVED ou CLOSED) → lança WorkflowAdvanceNotAllowedException
2. Chama resolveNextStatus(current)           → [VARIÁVEL — instância define]
3. Chama onBeforeAdvance(manifestation, next) → [HOOK — instância pode observar]
4. Retorna WorkflowStepResult(nextStatus, deadline)
```

### Algoritmo fixo de `appeal()`

```
1. Se isAppealAllowed(current) == false → lança WorkflowAppealNotAllowedException
2. Chama resolveAppealStatus(current)         → [VARIÁVEL — instância define]
3. Chama onBeforeAppeal(manifestation, next)  → [HOOK — instância pode observar]
4. Retorna WorkflowStepResult(nextStatus, deadline)
```

---

## Implementação padrão (Core)

`DefaultWorkflowTemplate` — progressão linear, sem recurso, sem prazo.
Registrada em `CoreAutoConfiguration` via `@ConditionalOnMissingBean(WorkflowTemplate.class)`.

| Fase atual | Próxima fase |
|---|---|
| REGISTERED | IN_REVIEW |
| IN_REVIEW | RESOLVED |
| RESOLVED | — (terminal) |
| CLOSED | — (terminal) |

---

## Endpoints

| Método | Path | Descrição |
|---|---|---|
| POST | `/v1/workflow/{id}/advance` | Avança para a próxima fase |
| POST | `/v1/workflow/{id}/appeal` | Registra recurso |

Ambos retornam `ManifestationResponse` com o novo status.

---

## Como cada instância implementa

### Instância 1 — Compliance Corporativo

```java
@Component
public class ComplianceWorkflowTemplate extends WorkflowTemplate {

    @Override
    protected ManifestationStatus resolveNextStatus(ManifestationStatus current) {
        return switch (current) {
            case REGISTERED -> IN_REVIEW;
            case IN_REVIEW  -> RESOLVED;
            default         -> CLOSED;
        };
    }

    @Override
    protected boolean isAppealAllowed(ManifestationStatus status) {
        return false; // investigação sigilosa, sem recurso simples
    }

    @Override
    protected Duration deadlineFor(ManifestationStatus status) {
        return switch (status) {
            case IN_REVIEW -> Duration.ofDays(30);
            case RESOLVED  -> Duration.ofDays(5);
            default        -> null;
        };
    }

    @Override
    protected ManifestationStatus resolveAppealStatus(ManifestationStatus current) {
        throw new WorkflowAppealNotAllowedException(null); // nunca chamado
    }

    // Hook: registra auditoria a cada avanço
    @Override
    protected void onBeforeAdvance(Manifestation manifestation, ManifestationStatus next) {
        auditService.record(manifestation.getId(), "STATUS_CHANGED", next.name());
    }
}
```

### Instância 3 — Atendimento Público (LAI)

```java
@Component
public class PublicServiceWorkflowTemplate extends WorkflowTemplate {

    @Override
    protected boolean isAppealAllowed(ManifestationStatus status) {
        return status == IN_REVIEW || status == RESOLVED; // 3 instâncias de recurso
    }

    @Override
    protected ManifestationStatus resolveAppealStatus(ManifestationStatus current) {
        return IN_REVIEW; // retorna para análise
    }

    @Override
    protected Duration deadlineFor(ManifestationStatus status) {
        return Duration.ofDays(20); // prazo LAI: 20 dias úteis
    }

    @Override
    protected ManifestationStatus resolveNextStatus(ManifestationStatus current) {
        return switch (current) {
            case REGISTERED -> IN_REVIEW;
            case IN_REVIEW  -> RESOLVED;
            default         -> CLOSED;
        };
    }
}
```

---

## Uso dos hooks

Os hooks `onBeforeAdvance` e `onBeforeAppeal` são o ponto de extensão para comportamentos transversais:

| Hook | Casos de uso |
|---|---|
| `onBeforeAdvance` | Registrar auditoria, enviar notificação, verificar prazo |
| `onBeforeAppeal` | Registrar recurso no histórico, notificar responsável |

---

## Diagrama de classes

Ver [`docs/uml/workflow-template.puml`](uml/workflow-template.puml).

---

## Arquivos relevantes no Core

| Arquivo | Responsabilidade |
|---|---|
| `workflow/WorkflowTemplate.java` | Abstract class — esqueleto do algoritmo |
| `workflow/WorkflowStepResult.java` | `record(nextStatus, deadline)` |
| `workflow/DefaultWorkflowTemplate.java` | Implementação padrão (linear, sem recurso) |
| `exception/WorkflowAdvanceNotAllowedException.java` | Status terminal ou sem próxima fase |
| `exception/WorkflowAppealNotAllowedException.java` | Recurso não permitido |
| `service/WorkflowService.java` | Interface do serviço |
| `service/WorkflowServiceImpl.java` | Delega para `WorkflowTemplate` |
| `web/WorkflowController.java` | `POST /v1/workflow/{id}/advance` e `/appeal` |
| `config/CoreAutoConfiguration.java` | Registra `DefaultWorkflowTemplate` via `@ConditionalOnMissingBean` |
