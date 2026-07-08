package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.ManifestationAccusation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManifestationAccusationRepository
        extends JpaRepository<ManifestationAccusation, Long> {

    List<ManifestationAccusation> findByManifestationId(Long manifestationId);
}
