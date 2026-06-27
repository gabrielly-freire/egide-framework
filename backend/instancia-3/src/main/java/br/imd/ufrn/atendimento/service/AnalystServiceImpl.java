package br.imd.ufrn.atendimento.service;

import br.imd.ufrn.atendimento.domain.Analyst;
import br.imd.ufrn.atendimento.dto.AnalystRequest;
import br.imd.ufrn.atendimento.dto.AnalystResponse;
import br.imd.ufrn.atendimento.exception.AnalystNotFoundException;
import br.imd.ufrn.atendimento.mapper.AnalystMapper;
import br.imd.ufrn.atendimento.persistence.AnalystRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AnalystServiceImpl implements AnalystService {

    private final AnalystRepository repository;
    private final AnalystMapper mapper;

    @Override
    public AnalystResponse create(AnalystRequest request) {
        Analyst entity = mapper.toEntity(request);
        entity.setActive(true);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public AnalystResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AnalystNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalystResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public AnalystResponse update(Long id, AnalystRequest request) {
        Analyst entity = repository.findById(id)
                .orElseThrow(() -> new AnalystNotFoundException(id));
        entity.setName(request.name());
        entity.setSpecialty(request.specialty());
        entity.setRegion(request.region());
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        Analyst entity = repository.findById(id)
                .orElseThrow(() -> new AnalystNotFoundException(id));
        entity.setActive(false);
        repository.save(entity);
    }
}
