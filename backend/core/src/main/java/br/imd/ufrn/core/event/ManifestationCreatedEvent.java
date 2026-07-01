package br.imd.ufrn.core.event;

/**
 * Evento de domínio publicado pelo Core logo após uma manifestação ser registrada.
 *
 * <p>Serve de ponto de extensão (síncrono) para as instâncias reagirem ao registro sem que o Core
 * conheça suas decisões de infraestrutura. A Instância 1, por exemplo, escuta este evento de forma
 * assíncrona ({@code @Async @TransactionalEventListener}) para disparar a categorização por IA.
 */
public record ManifestationCreatedEvent(Long manifestationId) {}
