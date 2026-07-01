package br.imd.ufrn.service;

import br.imd.ufrn.domain.AcademicMember;
import br.imd.ufrn.dto.AcademicMemberRequest;
import br.imd.ufrn.dto.AcademicMemberResponse;
import br.imd.ufrn.exception.AcademicMemberNotFoundException;
import br.imd.ufrn.mapper.AcademicMemberMapper;
import br.imd.ufrn.persistence.AcademicMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AcademicMemberServiceImpl implements AcademicMemberService {

    private final AcademicMemberRepository repository;
    private final AcademicMemberMapper mapper;

    @Override
    public AcademicMemberResponse create(AcademicMemberRequest request) {
        AcademicMember entity = mapper.toEntity(request);
        entity.setActive(true);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicMemberResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AcademicMemberNotFoundException(id));
    }
}
