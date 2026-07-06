package br.imd.ufrn.core.service;

import br.imd.ufrn.core.domain.Party;
import br.imd.ufrn.core.dto.PartyRequest;
import br.imd.ufrn.core.dto.PartyResponse;
import br.imd.ufrn.core.exception.PartyNotFoundException;
import br.imd.ufrn.core.mapper.PartyMapper;
import br.imd.ufrn.core.persistence.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService {

    private final PartyRepository repository;
    private final PartyMapper mapper;

    @Override
    public PartyResponse create(PartyRequest request) {
        Party entity = mapper.toEntity(request);
        entity.setActive(true);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public PartyResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new PartyNotFoundException(id));
    }
}
