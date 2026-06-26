package br.imd.ufrn.core.service;

import br.imd.ufrn.core.domain.DecisionRecord;
import br.imd.ufrn.core.dto.DecisionRecordRequest;
import br.imd.ufrn.core.dto.DecisionRecordResponse;
import br.imd.ufrn.core.exception.DecisionRecordNotFoundException;
import br.imd.ufrn.core.mapper.DecisionRecordMapper;
import br.imd.ufrn.core.persistence.DecisionRecordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DecisionRecordServiceImpl implements DecisionRecordService {

    private final DecisionRecordRepository repository;
    private final DecisionRecordMapper mapper;

    @Override
    public DecisionRecordResponse create(DecisionRecordRequest request) {
        DecisionRecord entity = mapper.toEntity(request);
        entity.setActive(true);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionRecordResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new DecisionRecordNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DecisionRecordResponse> findAllByManifestationId(Long manifestationId) {
        return repository.findAllByManifestationId(manifestationId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        DecisionRecord entity = repository.findById(id)
                .orElseThrow(() -> new DecisionRecordNotFoundException(id));
        entity.setActive(false);
        repository.save(entity);
    }
}
