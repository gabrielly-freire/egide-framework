package br.imd.ufrn.atendimento.persistence;

import br.imd.ufrn.atendimento.domain.LegalImpediment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalImpedimentRepository extends JpaRepository<LegalImpediment, Long> {

    boolean existsByManifestationIdAndAnalystId(Long manifestationId, Long analystId);

    List<LegalImpediment> findAllByManifestationId(Long manifestationId);
}
