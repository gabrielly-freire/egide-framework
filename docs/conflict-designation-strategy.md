# Conflito de Interesse e Designação de Responsável — Padrão Strategy

## Visão geral

Dois pontos variáveis independentes — **critérios de conflito de interesse** e **regras de
designação** — são implementados como estratégias distintas. O Core orquestra ambas em
`DesignationServiceImpl`: a estratégia de designação determina *quem* deve ser atribuído;
a estratégia de conflito valida se essa atribuição é permitida.

---

## ConflictOfInterestStrategy

### Responsabilidade

Verificar se um analista está **impedido** de atuar em determinada manifestação.

### Interface

```java
public interface ConflictOfInterestStrategy {
    boolean hasConflict(ConflictOfInterestContext context);
}
```

### Contexto

```java
public record ConflictOfInterestContext(
    Long manifestationId,
    Long analystId,
    String manifestationType
) {}
```

O Core fornece os dados que conhece. A implementação da instância pode consultar suas próprias
fontes de dados (hierarquia, departamento, vínculos legais) usando `analystId` como chave.

### Implementação padrão

`NoConflictOfInterestStrategy` — retorna sempre `false` (sem restrição).

### Exemplos por instância

| Instância | Classe | Critério |
|---|---|---|
| Compliance | `HierarchyConflictStrategy` | Analista subordinado ao denunciado ou ao denunciante |
| Universidade | `DepartmentConflictStrategy` | Analista no mesmo centro/departamento do denunciado |
| Serviço Público | `LegalConflictStrategy` | Impedimento por citação ou parentesco (LAI) |

---

## DesignationStrategy

### Responsabilidade

Determinar **automaticamente** o analista responsável por uma manifestação.
Retornar `null` sinaliza que a designação deve ser feita manualmente via `POST /v1/assignments`.

### Interface

```java
public interface DesignationStrategy {
    Long resolve(DesignationContext context);
}
```

### Contexto

```java
public record DesignationContext(
    Long manifestationId,
    String manifestationType
) {}
```

### Implementação padrão

`ManualDesignationStrategy` — retorna sempre `null`.

### Exemplos por instância

| Instância | Classe | Lógica |
|---|---|---|
| Compliance | `RoleBasedDesignationStrategy` | Analista com perfil de investigação compatível com o tipo |
| Serviço Público | `RegionDesignationStrategy` | Designação por especialidade do órgão ou região afetada |

---

## DesignationServiceImpl — fluxo de auto-designação

```
POST /v1/designations/{manifestationId}/auto
         │
         ▼
1. Localiza manifestação (ou 404)
         │
         ▼
2. designationStrategy.resolve(context)
   ├── null? → 422 AutoAssignmentUnavailableException
   └── responsibleId
         │
         ▼
3. conflictOfInterestStrategy.hasConflict(context)
   ├── true? → 422 ConflictOfInterestException
   └── false
         │
         ▼
4. assignmentService.assign(manifestationId, responsibleId, null)
         │
         ▼
5. 201 ResponsibleAssignmentResponse
```

---

## Endpoints

| Método | Path | Status | Descrição |
|---|---|---|---|
| POST | `/v1/designations/{id}/auto` | 201 | Designa automaticamente via estratégia |
| GET | `/v1/designations/{id}/conflict/{analystId}` | 200 | Verifica conflito de interesse |

---

## Como implementar em uma instância

```java
// Conflito de interesse — Instância 2 (Universidade)
@Component
public class DepartmentConflictStrategy implements ConflictOfInterestStrategy {

    private final AnalystRepository analystRepository;

    @Override
    public boolean hasConflict(ConflictOfInterestContext context) {
        String analystDepartment = analystRepository
            .findDepartmentById(context.analystId());
        String accusedDepartment = manifestationRepository
            .findAccusedDepartment(context.manifestationId());
        return analystDepartment.equals(accusedDepartment);
    }
}

// Designação automática — Instância 3 (Serviço Público)
@Component
public class RegionDesignationStrategy implements DesignationStrategy {

    private final AnalystRepository analystRepository;

    @Override
    public Long resolve(DesignationContext context) {
        return analystRepository
            .findFirstBySpecialtyAndType(context.manifestationType())
            .map(Analyst::getId)
            .orElse(null); // null → designação manual como fallback
    }
}
```

Registrando via `@Component`, o `@ConditionalOnMissingBean` do Core ignora automaticamente os
beans padrão (`NoConflictOfInterestStrategy` e `ManualDesignationStrategy`).

---

## Diagrama de classes

Ver [`docs/uml/conflict-designation-strategy.puml`](uml/conflict-designation-strategy.puml).

---

## Arquivos relevantes no Core

| Arquivo | Responsabilidade |
|---|---|
| `conflict/ConflictOfInterestStrategy.java` | Interface da estratégia de conflito |
| `conflict/ConflictOfInterestContext.java` | Contexto: manifestação + analista + tipo |
| `conflict/NoConflictOfInterestStrategy.java` | Padrão: sem restrições |
| `designation/DesignationStrategy.java` | Interface da estratégia de designação |
| `designation/DesignationContext.java` | Contexto: manifestação + tipo |
| `designation/ManualDesignationStrategy.java` | Padrão: sempre manual (null) |
| `exception/ConflictOfInterestException.java` | 422 quando conflito detectado |
| `exception/AutoAssignmentUnavailableException.java` | 422 quando estratégia retorna null |
| `dto/ConflictCheckResponse.java` | Resposta do endpoint de verificação |
| `service/DesignationService.java` | Interface: autoAssign + hasConflict |
| `service/DesignationServiceImpl.java` | Orquestra as duas estratégias |
| `web/DesignationController.java` | `/v1/designations/{id}/auto` e `/conflict/{analystId}` |
| `config/CoreAutoConfiguration.java` | Registra os dois beans padrão |
