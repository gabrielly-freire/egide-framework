package br.imd.ufrn.web;

import br.imd.ufrn.dto.AcademicMemberRequest;
import br.imd.ufrn.dto.AcademicMemberResponse;
import br.imd.ufrn.service.AcademicMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/academic-members")
@RequiredArgsConstructor
public class AcademicMemberController {

    private final AcademicMemberService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcademicMemberResponse create(@Valid @RequestBody AcademicMemberRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public AcademicMemberResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
