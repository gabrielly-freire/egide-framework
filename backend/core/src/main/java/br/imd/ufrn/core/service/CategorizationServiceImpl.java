package br.imd.ufrn.core.service;

import br.imd.ufrn.core.categorization.CategorizationContext;
import br.imd.ufrn.core.categorization.CategorizationResult;
import br.imd.ufrn.core.categorization.CategorizationStrategy;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.dto.ManifestationResponse;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.mapper.ManifestationMapper;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategorizationServiceImpl implements CategorizationService {

    private final ManifestationRepository repository;
    private final ManifestationMapper mapper;
    private final CategorizationStrategy categorizationStrategy;

    @Override
    public ManifestationResponse categorize(Long manifestationId) {
        Manifestation manifestation = repository.findById(manifestationId)
                .orElseThrow(() -> new ManifestationNotFoundException(manifestationId));

        CategorizationResult result = categorizationStrategy.categorize(new CategorizationContext(
                manifestation.getId(),
                manifestation.getTitle(),
                manifestation.getDescription(),
                manifestation.getType()));

        manifestation.setCategory(result.category());
        manifestation.setRiskLevel(result.riskLevel());
        return mapper.toResponse(repository.save(manifestation));
    }
}
