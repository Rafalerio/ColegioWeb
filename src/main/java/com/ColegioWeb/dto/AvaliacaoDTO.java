package com.ColegioWeb.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AvaliacaoDTO(
        @NotNull(message = "O aluno é obrigatório")
        Long alunoId,

        @NotNull(message = "A disciplina é obrigatória")
        Long disciplinaId,

        @NotNull(message = "A nota é obrigatória")
        Double nota,

        String observacao,

        @NotNull(message = "A data da avaliação é obrigatória")
        LocalDate dataAvaliacao
) {}
