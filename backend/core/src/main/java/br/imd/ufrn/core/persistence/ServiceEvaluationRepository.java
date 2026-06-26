package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.ServiceEvaluation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceEvaluationRepository extends JpaRepository<ServiceEvaluation, Long> {

    Optional<ServiceEvaluation> findByManifestationId(Long manifestationId);

    boolean existsByManifestationId(Long manifestationId);
}
