package br.imd.ufrn.service;

import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.domain.ManifestationAccusation;
import br.imd.ufrn.dto.AccusationRequest;
import br.imd.ufrn.dto.AccusationResponse;
import br.imd.ufrn.exception.AcademicMemberNotFoundException;
import br.imd.ufrn.exception.AccusationNotFoundException;
import br.imd.ufrn.mapper.ManifestationAccusationMapper;
import br.imd.ufrn.persistence.AcademicMemberRepository;
import br.imd.ufrn.persistence.ManifestationAccusationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccusationServiceImpl implements AccusationService {

    private final ManifestationAccusationRepository accusationRepository;
    private final ManifestationRepository manifestationRepository;
    private final AcademicMemberRepository memberRepository;
    private final ManifestationAccusationMapper mapper;

    @Override
    public AccusationResponse register(Long manifestationId, AccusationRequest request) {
        if (!manifestationRepository.existsById(manifestationId)) {
            throw new ManifestationNotFoundException(manifestationId);
        }
        if (!memberRepository.existsById(request.accusedMemberId())) {
            throw new AcademicMemberNotFoundException(request.accusedMemberId());
        }

        ManifestationAccusation entity = accusationRepository
                .findByManifestationId(manifestationId)
                .orElseGet(ManifestationAccusation::new);
        entity.setManifestationId(manifestationId);
        entity.setAccusedMemberId(request.accusedMemberId());
        entity.setActive(true);

        return mapper.toResponse(accusationRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public AccusationResponse findByManifestationId(Long manifestationId) {
        return accusationRepository.findByManifestationId(manifestationId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AccusationNotFoundException(manifestationId));
    }
}
