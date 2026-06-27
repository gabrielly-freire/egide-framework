package br.imd.ufrn.core.config;

import br.imd.ufrn.core.anonymization.AnonymizationStrategy;
import br.imd.ufrn.core.anonymization.TransparentAnonymizationStrategy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AnonymizationStrategy.class)
    public AnonymizationStrategy transparentAnonymizationStrategy() {
        return new TransparentAnonymizationStrategy();
    }
}
