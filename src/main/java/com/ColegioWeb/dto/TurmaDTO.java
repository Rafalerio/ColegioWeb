package com.ColegioWeb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TurmaDTO(
        Long id,
        
        @NotBlank(message = "O nome da turma é obrigatório")
        String nome,

        @NotBlank(message = "A série é obrigatória")
        String serie,

        @NotBlank(message = "O turno é obrigatório")
        String turno,

        @NotNull(message = "O ano letivo é obrigatório")
        Integer anoLetivo,

        Integer capacidadeMax
) {
}
