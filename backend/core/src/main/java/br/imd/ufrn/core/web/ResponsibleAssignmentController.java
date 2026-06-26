package br.imd.ufrn.core.web;

import br.imd.ufrn.core.dto.ResponsibleAssignmentRequest;
import br.imd.ufrn.core.dto.ResponsibleAssignmentResponse;
import br.imd.ufrn.core.service.ResponsibleAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/assignments")
@RequiredArgsConstructor
public class ResponsibleAssignmentController {

    private final ResponsibleAssignmentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponsibleAssignmentResponse assign(@Valid @RequestBody ResponsibleAssignmentRequest request) {
        return service.assign(request);
    }

    @GetMapping("/{id}")
    public ResponsibleAssignmentResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/manifestation/{manifestationId}")
    public ResponsibleAssignmentResponse findByManifestationId(@PathVariable Long manifestationId) {
        return service.findByManifestationId(manifestationId);
    }

    @DeleteMapping("/manifestation/{manifestationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassign(@PathVariable Long manifestationId) {
        service.unassign(manifestationId);
    }
}
