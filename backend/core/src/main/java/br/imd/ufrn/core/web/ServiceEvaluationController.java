package br.imd.ufrn.core.web;

import br.imd.ufrn.core.dto.ServiceEvaluationRequest;
import br.imd.ufrn.core.dto.ServiceEvaluationResponse;
import br.imd.ufrn.core.service.ServiceEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/evaluations")
@RequiredArgsConstructor
public class ServiceEvaluationController {

    private final ServiceEvaluationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceEvaluationResponse create(@Valid @RequestBody ServiceEvaluationRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ServiceEvaluationResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/manifestation/{manifestationId}")
    public ServiceEvaluationResponse findByManifestationId(@PathVariable Long manifestationId) {
        return service.findByManifestationId(manifestationId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
