package br.imd.ufrn.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManifestationRequest(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 500, message = "O título deve ter no máximo 500 caracteres")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotBlank(message = "O tipo é obrigatório")
        @Size(max = 100, message = "O tipo deve ter no máximo 100 caracteres")
        String type,

        Boolean anonymous
) {
    public ManifestationRequest {
        if (anonymous == null) {
            anonymous = false;
        }
    }
}
