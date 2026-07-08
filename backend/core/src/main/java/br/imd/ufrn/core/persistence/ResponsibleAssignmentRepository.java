package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.ResponsibleAssignment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsibleAssignmentRepository extends JpaRepository<ResponsibleAssignment, Long> {

    Optional<ResponsibleAssignment> findByManifestationId(Long manifestationId);

    Page<ResponsibleAssignment> findByResponsibleId(Long responsibleId, Pageable pageable);
}
