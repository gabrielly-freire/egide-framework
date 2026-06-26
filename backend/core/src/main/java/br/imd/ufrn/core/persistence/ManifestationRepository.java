package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.Manifestation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManifestationRepository extends JpaRepository<Manifestation, Long> {

    Optional<Manifestation> findByProtocolNumber(String protocolNumber);

    boolean existsByProtocolNumber(String protocolNumber);

    @Query("SELECT m.status, COUNT(m) FROM Manifestation m GROUP BY m.status")
    List<Object[]> countGroupedByStatus();

    @Query("SELECT m.type, COUNT(m) FROM Manifestation m GROUP BY m.type")
    List<Object[]> countGroupedByType();

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT m.status, COUNT(m) FROM Manifestation m WHERE m.createdAt BETWEEN :from AND :to GROUP BY m.status")
    List<Object[]> countGroupedByStatusBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT m.type, COUNT(m) FROM Manifestation m WHERE m.createdAt BETWEEN :from AND :to GROUP BY m.type")
    List<Object[]> countGroupedByTypeBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
