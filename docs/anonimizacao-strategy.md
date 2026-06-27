# Anonimização — Padrão Strategy

## Visão geral

A anonimização é um **ponto variável**: cada instância define suas próprias regras de mascaramento do texto da manifestação. O Core aplica a estratégia — as instâncias a implementam.

O alvo da anonimização é o campo `description` (relato), onde dados pessoais identificáveis podem aparecer embutidos no texto (ex.: nomes, cargos, matrículas).

---

## Participantes do padrão

| Participante | Classe / Interface | Módulo |
|---|---|---|
| **Strategy** | `AnonymizationStrategy` | Core |
| **Context** | `ManifestationServiceImpl` | Core |
| **ConcreteStrategy (padrão)** | `TransparentAnonymizationStrategy` | Core |
| **ConcreteStrategy (instância-1)** | `ComplianceAnonymizationStrategy` | instancia-1 |
| **ConcreteStrategy (instância-2)** | `UniversityAnonymizationStrategy` | instancia-2 |
| **Dados de contexto** | `AnonymizationContext` | Core |

---

## Interface da Strategy

```java
public interface AnonymizationStrategy {
    String anonymize(String text, AnonymizationContext context);
}
```

### Contexto disponível para decisão

```java
public record AnonymizationContext(
    boolean anonymous,  // manifestante optou pelo anonimato?
    String type         // tipo da manifestação (ex.: ASSÉDIO, FRAUDE)
)
```

O contexto permite que a implementação decida **se** e **como** anonimizar com base na natureza da manifestação.

---

## Como o Core aplica a estratégia

Em `ManifestationServiceImpl`, antes de persistir:

```java
entity.setDescription(
    anonymizationStrategy.anonymize(
        request.description(),
        new AnonymizationContext(request.anonymous(), request.type())
    )
);
```

Isso ocorre tanto em `create()` quanto em `update()`.

---

## Implementação padrão (Core)

`TransparentAnonymizationStrategy` retorna o texto inalterado. É registrada via `@ConditionalOnMissingBean` em `CoreAutoConfiguration`, garantindo que o Core funcione sem exigir uma implementação da instância imediatamente.

```java
public class TransparentAnonymizationStrategy implements AnonymizationStrategy {
    @Override
    public String anonymize(String text, AnonymizationContext context) {
        return text;
    }
}
```

---

## Como cada instância substitui o padrão

A instância declara um `@Bean` do tipo `AnonymizationStrategy`. O `@ConditionalOnMissingBean` do Core faz com que o bean da instância tome precedência automaticamente.

### Instância 1 — Compliance Corporativo

Chama um microserviço externo de IA para mascaramento estrito. A anonimização só ocorre quando o manifestante escolhe o anonimato.

```java
@Service
public class ComplianceAnonymizationStrategy implements AnonymizationStrategy {

    private final AiServiceClient aiClient;

    @Override
    public String anonymize(String text, AnonymizationContext context) {
        if (!context.anonymous()) {
            return text;
        }
        return aiClient.anonymize(text, context.type());
    }
}
```

O `context.type()` é repassado ao microserviço para que ele aplique regras específicas por categoria (assédio, fraude, corrupção etc.).

### Instância 2 — Ouvidoria Universitária

Identidade visível apenas para a Ouvidoria Geral. A lógica de mascaramento parcial é definida pela própria instância, sem depender de serviço externo.

```java
@Service
public class UniversityAnonymizationStrategy implements AnonymizationStrategy {

    @Override
    public String anonymize(String text, AnonymizationContext context) {
        if (!context.anonymous()) {
            return text;
        }
        return PartialMasker.mask(text); // lógica interna da instância
    }
}
```

---

## Diagrama de classes

Ver [`docs/uml/anonymization-strategy.puml`](uml/anonymization-strategy.puml).

---

## Arquivos relevantes no Core

| Arquivo | Responsabilidade |
|---|---|
| `anonymization/AnonymizationStrategy.java` | Interface da strategy |
| `anonymization/AnonymizationContext.java` | Dados de contexto para decisão |
| `anonymization/TransparentAnonymizationStrategy.java` | Implementação padrão (no-op) |
| `config/CoreAutoConfiguration.java` | Registra o bean padrão via `@ConditionalOnMissingBean` |
| `service/ManifestationServiceImpl.java` | Context — aplica a strategy em `create()` e `update()` |
| `resources/META-INF/spring/…AutoConfiguration.imports` | Registra `CoreAutoConfiguration` para auto-descoberta |
