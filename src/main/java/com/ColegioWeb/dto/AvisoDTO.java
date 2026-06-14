package com.ColegioWeb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AvisoDTO(
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        String conteudo,

        Long turmaId
) {}
