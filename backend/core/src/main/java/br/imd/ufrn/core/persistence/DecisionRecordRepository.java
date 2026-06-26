package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.DecisionRecord;
import br.imd.ufrn.core.domain.DecisionType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionRecordRepository extends JpaRepository<DecisionRecord, Long> {

    List<DecisionRecord> findAllByManifestationId(Long manifestationId);

    long countByType(DecisionType type);

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByTypeAndCreatedAtBetween(DecisionType type, LocalDateTime from, LocalDateTime to);
}
