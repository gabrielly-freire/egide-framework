package br.imd.ufrn.atendimento.web;

import br.imd.ufrn.atendimento.domain.Analyst;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ResponsibleAssignment;
import br.imd.ufrn.core.dto.ManifestationResponse;
import br.imd.ufrn.core.mapper.ManifestationMapper;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.core.persistence.ResponsibleAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Um analista (role ANALYST) só deve enxergar as manifestações em que ele é o responsável
 * designado — ver todas as manifestações da instância é privilégio de ADMIN, via
 * {@code GET /v1/manifestations} (restrito a ADMIN em {@code SecurityConfig}).
 */
@RestController
@RequestMapping("/v1/manifestations")
@RequiredArgsConstructor
public class AssignedManifestationController {

    private final ResponsibleAssignmentRepository responsibleAssignmentRepository;
    private final ManifestationRepository manifestationRepository;
    private final ManifestationMapper manifestationMapper;

    @GetMapping("/mine")
    public Page<ManifestationResponse> findMine(
            @AuthenticationPrincipal Analyst analyst,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ResponsibleAssignment> assignments =
                responsibleAssignmentRepository.findByResponsibleId(analyst.getId(), pageable);

        return assignments.map(assignment -> {
            Manifestation manifestation = manifestationRepository.findById(assignment.getManifestationId())
                    .orElseThrow();
            return manifestationMapper.toResponse(manifestation);
        });
    }
}
