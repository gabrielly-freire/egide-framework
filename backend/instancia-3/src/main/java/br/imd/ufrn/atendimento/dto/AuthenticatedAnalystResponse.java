package br.imd.ufrn.atendimento.dto;

import br.imd.ufrn.atendimento.domain.AnalystRole;

public record AuthenticatedAnalystResponse(
        Long id,
        String name,
        String email,
        AnalystRole role
) {}
