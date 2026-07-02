package br.imd.ufrn.atendimento.web;

import br.imd.ufrn.atendimento.dto.LegalImpedimentRequest;
import br.imd.ufrn.atendimento.dto.LegalImpedimentResponse;
import br.imd.ufrn.atendimento.service.LegalImpedimentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/legal-impediments")
@RequiredArgsConstructor
public class LegalImpedimentController {

    private final LegalImpedimentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LegalImpedimentResponse register(@RequestBody @Valid LegalImpedimentRequest request) {
        return service.register(request);
    }

    @GetMapping("/{id}")
    public LegalImpedimentResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/manifestation/{manifestationId}")
    public List<LegalImpedimentResponse> findByManifestation(@PathVariable Long manifestationId) {
        return service.findByManifestationId(manifestationId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        service.remove(id);
    }
}
