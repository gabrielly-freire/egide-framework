package br.imd.ufrn.core.web;

import br.imd.ufrn.core.dto.ManifestationResponse;
import br.imd.ufrn.core.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService service;

    @PostMapping("/{manifestationId}/advance")
    public ManifestationResponse advance(@PathVariable Long manifestationId) {
        return service.advance(manifestationId);
    }

    @PostMapping("/{manifestationId}/appeal")
    public ManifestationResponse appeal(@PathVariable Long manifestationId) {
        return service.appeal(manifestationId);
    }
}
