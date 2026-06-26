package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.DecisionRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionRecordRepository extends JpaRepository<DecisionRecord, Long> {

    List<DecisionRecord> findAllByManifestationId(Long manifestationId);
}
