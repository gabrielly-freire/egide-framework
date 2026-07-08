package br.imd.ufrn.core.web;

import br.imd.ufrn.core.dto.PartyRequest;
import br.imd.ufrn.core.dto.PartyResponse;
import br.imd.ufrn.core.service.PartyService;
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
@RequestMapping("/v1/parties")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartyResponse create(@Valid @RequestBody PartyRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public PartyResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
