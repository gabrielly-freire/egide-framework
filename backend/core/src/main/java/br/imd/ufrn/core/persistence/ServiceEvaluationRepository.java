package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.ServiceEvaluation;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceEvaluationRepository extends JpaRepository<ServiceEvaluation, Long> {

    Optional<ServiceEvaluation> findByManifestationId(Long manifestationId);

    boolean existsByManifestationId(Long manifestationId);

    @Query("SELECT AVG(e.rating) FROM ServiceEvaluation e")
    Double averageRating();

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT AVG(e.rating) FROM ServiceEvaluation e WHERE e.createdAt BETWEEN :from AND :to")
    Double averageRatingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
