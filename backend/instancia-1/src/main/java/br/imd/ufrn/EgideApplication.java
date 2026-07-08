package br.imd.ufrn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

// TODO(dívida técnica): o Core ganhou um ponto fixo de acusação/Party genérico
// (br.imd.ufrn.core.web.AccusationController / core.service.AccusationServiceImpl) que colide
// (mesma rota /v1/manifestations/{id}/accusations) com a implementação própria da Instância 1
// (br.imd.ufrn.conflict.*), construída antes dessa mudança chegar via merge do origin/main.
// Excluídos aqui para manter ativa a implementação da instância; migrar a Instância 1 para o
// Party do Core fica como próximo passo (fora do escopo de agora). O repositório do Core
// (ManifestationAccusationRepository) continua ativo — o próprio DesignationServiceImpl do Core
// depende dele — e o nosso repositório equivalente foi renomeado para
// ComplianceAccusationRepository para não colidir o bean name.
@SpringBootApplication
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        br.imd.ufrn.core.web.AccusationController.class,
                        br.imd.ufrn.core.service.AccusationServiceImpl.class
                }
        )
)
public class EgideApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgideApplication.class, args);
    }
}
