package br.imd.ufrn.core.persistence;

import br.imd.ufrn.core.domain.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {
}
