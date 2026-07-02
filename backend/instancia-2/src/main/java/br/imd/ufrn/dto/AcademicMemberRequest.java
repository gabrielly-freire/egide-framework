package br.imd.ufrn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AcademicMemberRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String name,

        @NotBlank(message = "A unidade é obrigatória")
        @Size(max = 100, message = "A unidade deve ter no máximo 100 caracteres")
        String unit
) {}
