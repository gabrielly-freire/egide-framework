package br.imd.ufrn.atendimento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"br.imd.ufrn.atendimento", "br.imd.ufrn.core"})
@EntityScan(basePackages = {"br.imd.ufrn.atendimento.domain", "br.imd.ufrn.core.domain"})
@EnableJpaRepositories(basePackages = {"br.imd.ufrn.atendimento.persistence", "br.imd.ufrn.core.persistence"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
