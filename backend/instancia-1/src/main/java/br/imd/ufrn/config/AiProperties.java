package br.imd.ufrn.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração de conexão com o microserviço de IA (prefixo {@code ai} no application.yml).
 * Espelha o AiConnectionProperties do monolito original; os timeouts evitam bloqueio
 * indefinido da thread que chama a IA de forma síncrona.
 */
@ConfigurationProperties(prefix = "ai")
public record AiProperties(
        String baseUrl,
        String apiKey,
        int connectTimeoutMs,
        int readTimeoutMs
) {}
