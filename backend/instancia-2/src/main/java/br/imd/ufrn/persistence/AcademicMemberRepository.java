package br.imd.ufrn.persistence;

import br.imd.ufrn.domain.AcademicMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicMemberRepository extends JpaRepository<AcademicMember, Long> {
}
