package br.imd.ufrn.core.web;

import br.imd.ufrn.core.dto.ConflictCheckResponse;
import br.imd.ufrn.core.dto.ResponsibleAssignmentResponse;
import br.imd.ufrn.core.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/designations")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService service;

    @PostMapping("/{manifestationId}/auto")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponsibleAssignmentResponse autoAssign(@PathVariable Long manifestationId) {
        return service.autoAssign(manifestationId);
    }

    @GetMapping("/{manifestationId}/conflict/{analystId}")
    public ConflictCheckResponse checkConflict(
            @PathVariable Long manifestationId,
            @PathVariable Long analystId) {
        boolean hasConflict = service.hasConflict(manifestationId, analystId);
        return new ConflictCheckResponse(manifestationId, analystId, hasConflict);
    }
}
