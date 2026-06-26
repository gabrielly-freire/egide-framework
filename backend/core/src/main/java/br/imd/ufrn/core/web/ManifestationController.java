package br.imd.ufrn.core.web;

import br.imd.ufrn.core.dto.ManifestationRequest;
import br.imd.ufrn.core.dto.ManifestationResponse;
import br.imd.ufrn.core.service.ManifestationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/manifestations")
@RequiredArgsConstructor
public class ManifestationController {

    private final ManifestationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ManifestationResponse create(@Valid @RequestBody ManifestationRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ManifestationResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/protocol/{protocolNumber}")
    public ManifestationResponse findByProtocolNumber(@PathVariable String protocolNumber) {
        return service.findByProtocolNumber(protocolNumber);
    }

    @GetMapping
    public Page<ManifestationResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        return service.findAll(pageable);
    }

    @PutMapping("/{id}")
    public ManifestationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ManifestationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
