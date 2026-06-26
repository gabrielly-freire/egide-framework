package br.imd.ufrn.core.web;

import br.imd.ufrn.core.dto.AuditEntryRequest;
import br.imd.ufrn.core.dto.AuditEntryResponse;
import br.imd.ufrn.core.service.AuditEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/audit")
@RequiredArgsConstructor
public class AuditEntryController {

    private final AuditEntryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuditEntryResponse create(@Valid @RequestBody AuditEntryRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public AuditEntryResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/manifestation/{manifestationId}")
    public Page<AuditEntryResponse> findAllByManifestationId(
            @PathVariable Long manifestationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "occurredAt"));
        return service.findAllByManifestationId(manifestationId, pageable);
    }
}
