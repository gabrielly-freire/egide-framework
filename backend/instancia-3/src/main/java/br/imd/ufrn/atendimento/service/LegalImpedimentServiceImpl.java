package br.imd.ufrn.atendimento.service;

import br.imd.ufrn.atendimento.domain.LegalImpediment;
import br.imd.ufrn.atendimento.dto.LegalImpedimentRequest;
import br.imd.ufrn.atendimento.dto.LegalImpedimentResponse;
import br.imd.ufrn.atendimento.exception.LegalImpedimentNotFoundException;
import br.imd.ufrn.atendimento.mapper.LegalImpedimentMapper;
import br.imd.ufrn.atendimento.persistence.LegalImpedimentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LegalImpedimentServiceImpl implements LegalImpedimentService {

    private final LegalImpedimentRepository repository;
    private final LegalImpedimentMapper mapper;

    @Override
    public LegalImpedimentResponse register(LegalImpedimentRequest request) {
        LegalImpediment entity = mapper.toEntity(request);
        entity.setActive(true);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public LegalImpedimentResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new LegalImpedimentNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalImpedimentResponse> findByManifestationId(Long manifestationId) {
        return repository.findAllByManifestationId(manifestationId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void remove(Long id) {
        LegalImpediment entity = repository.findById(id)
                .orElseThrow(() -> new LegalImpedimentNotFoundException(id));
        entity.setActive(false);
        repository.save(entity);
    }
}
