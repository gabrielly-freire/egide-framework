package br.imd.ufrn.user;

import br.imd.ufrn.user.dto.CreateDepartmentRequest;
import br.imd.ufrn.user.dto.DepartmentResponse;
import java.util.List;

public interface DepartmentService {

    DepartmentResponse create(CreateDepartmentRequest request);

    List<DepartmentResponse> findAll();
}
