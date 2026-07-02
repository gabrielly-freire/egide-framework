package br.imd.ufrn.user;

import br.imd.ufrn.user.dto.CreateDepartmentRequest;
import br.imd.ufrn.user.dto.DepartmentResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;

    @Override
    public DepartmentResponse create(CreateDepartmentRequest request) {
        Department department = new Department();
        department.setName(request.name());
        department.setAcronym(request.acronym());
        return toResponse(repository.save(department));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    private DepartmentResponse toResponse(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getAcronym());
    }
}
