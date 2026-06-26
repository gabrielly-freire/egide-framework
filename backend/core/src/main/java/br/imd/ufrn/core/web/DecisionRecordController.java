package br.imd.ufrn.core.web;

import br.imd.ufrn.core.dto.DecisionRecordRequest;
import br.imd.ufrn.core.dto.DecisionRecordResponse;
import br.imd.ufrn.core.service.DecisionRecordService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/decisions")
@RequiredArgsConstructor
public class DecisionRecordController {

    private final DecisionRecordService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DecisionRecordResponse create(@Valid @RequestBody DecisionRecordRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public DecisionRecordResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/manifestation/{manifestationId}")
    public List<DecisionRecordResponse> findAllByManifestationId(@PathVariable Long manifestationId) {
        return service.findAllByManifestationId(manifestationId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
