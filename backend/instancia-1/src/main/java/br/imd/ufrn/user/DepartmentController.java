package br.imd.ufrn.user;

import br.imd.ufrn.user.dto.CreateDepartmentRequest;
import br.imd.ufrn.user.dto.DepartmentResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/departments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@Valid @RequestBody CreateDepartmentRequest request) {
        return departmentService.create(request);
    }

    @GetMapping
    public List<DepartmentResponse> findAll() {
        return departmentService.findAll();
    }
}
