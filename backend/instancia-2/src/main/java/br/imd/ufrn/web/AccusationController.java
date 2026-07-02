package br.imd.ufrn.web;

import br.imd.ufrn.dto.AccusationRequest;
import br.imd.ufrn.dto.AccusationResponse;
import br.imd.ufrn.service.AccusationService;
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
@RequestMapping("/v1/manifestations/{manifestationId}/accusation")
@RequiredArgsConstructor
public class AccusationController {

    private final AccusationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccusationResponse register(
            @PathVariable Long manifestationId,
            @Valid @RequestBody AccusationRequest request) {
        return service.register(manifestationId, request);
    }

    @GetMapping
    public AccusationResponse findByManifestationId(@PathVariable Long manifestationId) {
        return service.findByManifestationId(manifestationId);
    }
}
