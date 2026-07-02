package br.imd.ufrn.persistence;

import br.imd.ufrn.domain.ManifestationAccusation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManifestationAccusationRepository
        extends JpaRepository<ManifestationAccusation, Long> {

    Optional<ManifestationAccusation> findByManifestationId(Long manifestationId);
}
