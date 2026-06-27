package br.imd.ufrn.atendimento.persistence;

import br.imd.ufrn.atendimento.domain.Analyst;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalystRepository extends JpaRepository<Analyst, Long> {

    Optional<Analyst> findFirstBySpecialty(String specialty);
}
