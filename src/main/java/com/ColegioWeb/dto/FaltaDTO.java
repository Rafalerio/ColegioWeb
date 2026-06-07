package com.ColegioWeb.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record FaltaDTO(
        @NotNull(message = "O aluno é obrigatório")
        Long alunoId,

        @NotNull(message = "A disciplina é obrigatória")
        Long disciplinaId,

        @NotNull(message = "A data da falta é obrigatória")
        LocalDate dataFalta
) {}
