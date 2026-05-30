package com.ColegioWeb.dto;

import jakarta.validation.constraints.NotNull;

public record TransferenciaTurmaDTO(
        @NotNull(message = "O ID do aluno é obrigatório")
        Long alunoId,

        @NotNull(message = "O ID da nova turma é obrigatório")
        Long novaTurmaId
) {
}
