package br.imd.ufrn.conflict;

import br.imd.ufrn.conflict.dto.AccusationResponse;
import br.imd.ufrn.conflict.dto.CreateAccusationRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Registro dos acusados de uma manifestação (dado próprio da instância, base para o conflito de
 * interesse). Exige autenticação (herdado da política global de segurança).
 */
@RestController
@RequestMapping("/v1/manifestations/{manifestationId}/accusations")
@RequiredArgsConstructor
public class AccusationController {

    private final AccusationService accusationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccusationResponse create(
            @PathVariable Long manifestationId,
            @Valid @RequestBody CreateAccusationRequest request) {
        return accusationService.register(manifestationId, request.accusedUserId());
    }

    @GetMapping
    public List<AccusationResponse> findByManifestation(@PathVariable Long manifestationId) {
        return accusationService.findByManifestation(manifestationId);
    }
}
