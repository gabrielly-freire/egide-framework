package br.imd.ufrn.core.service;

import br.imd.ufrn.core.domain.ManifestationAccusation;
import br.imd.ufrn.core.dto.AccusationRequest;
import br.imd.ufrn.core.dto.AccusationResponse;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.mapper.ManifestationAccusationMapper;
import br.imd.ufrn.core.persistence.ManifestationAccusationRepository;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccusationServiceImpl implements AccusationService {

    private final ManifestationAccusationRepository accusationRepository;
    private final ManifestationRepository manifestationRepository;
    private final ManifestationAccusationMapper mapper;

    @Override
    public AccusationResponse register(Long manifestationId, AccusationRequest request) {
        if (!manifestationRepository.existsById(manifestationId)) {
            throw new ManifestationNotFoundException(manifestationId);
        }

        ManifestationAccusation entity = new ManifestationAccusation();
        entity.setManifestationId(manifestationId);
        entity.setAccusedPartyId(request.accusedPartyId());
        entity.setActive(true);

        return mapper.toResponse(accusationRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccusationResponse> findByManifestationId(Long manifestationId) {
        return accusationRepository.findByManifestationId(manifestationId).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
