package br.imd.ufrn.conflict;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Nome próprio (não ManifestationAccusationRepository): o Core ganhou seu próprio repositório com
// esse mesmo nome simples (br.imd.ufrn.core.persistence.ManifestationAccusationRepository), usado
// internamente por DesignationServiceImpl do Core — os dois bean names colidiam. Ver TODO em
// EgideApplication.
public interface ComplianceAccusationRepository extends JpaRepository<ManifestationAccusation, Long> {

    List<ManifestationAccusation> findByManifestationId(Long manifestationId);
}
