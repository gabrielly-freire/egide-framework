package br.imd.ufrn.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Monta o {@link RestClient} usado para falar com o microserviço de IA: baseia a URL, injeta o
 * header de autenticação {@code API-Key} em toda chamada e aplica os timeouts configurados.
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiClientConfig {

    @Bean
    public RestClient aiRestClient(AiProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(props.connectTimeoutMs());
        factory.setReadTimeout(props.readTimeoutMs());

        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("API-Key", props.apiKey())
                .requestFactory(factory)
                .build();
    }
}
