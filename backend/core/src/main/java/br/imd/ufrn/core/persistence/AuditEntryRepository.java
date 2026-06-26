package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.AuditEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEntryRepository extends JpaRepository<AuditEntry, Long> {

    Page<AuditEntry> findAllByManifestationId(Long manifestationId, Pageable pageable);
}
