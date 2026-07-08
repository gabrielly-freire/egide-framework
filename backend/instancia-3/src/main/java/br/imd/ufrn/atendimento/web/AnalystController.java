package br.imd.ufrn.atendimento.web;

import br.imd.ufrn.atendimento.dto.AnalystRequest;
import br.imd.ufrn.atendimento.dto.AnalystResponse;
import br.imd.ufrn.atendimento.service.AnalystService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/analysts")
@RequiredArgsConstructor
public class AnalystController {

    private final AnalystService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnalystResponse create(@RequestBody @Valid AnalystRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public AnalystResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<AnalystResponse> findAll() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    public AnalystResponse update(@PathVariable Long id, @RequestBody @Valid AnalystRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
