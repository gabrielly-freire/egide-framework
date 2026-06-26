package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.Manifestation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManifestationRepository extends JpaRepository<Manifestation, Long> {

    Optional<Manifestation> findByProtocolNumber(String protocolNumber);

    boolean existsByProtocolNumber(String protocolNumber);
}
