package com.ColegioWeb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TarefaDTO(
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotNull(message = "A data de entrega é obrigatória")
        LocalDate dataEntrega,

        @NotNull(message = "A turma é obrigatória")
        Long turmaId,

        @NotNull(message = "A disciplina é obrigatória")
        Long disciplinaId,

        @NotBlank(message = "O tipo é obrigatório")
        String tipo
) {}
