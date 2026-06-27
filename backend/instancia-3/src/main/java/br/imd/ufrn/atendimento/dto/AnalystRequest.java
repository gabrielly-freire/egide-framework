package br.imd.ufrn.atendimento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnalystRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String name,

        @NotBlank(message = "A especialidade é obrigatória")
        @Size(max = 100, message = "A especialidade deve ter no máximo 100 caracteres")
        String specialty,

        @NotBlank(message = "A região é obrigatória")
        @Size(max = 100, message = "A região deve ter no máximo 100 caracteres")
        String region
) {}
