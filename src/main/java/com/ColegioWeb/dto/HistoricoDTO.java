package com.ColegioWeb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HistoricoDTO(
    @NotNull(message = "ID do admin é obrigatório") Long adminId,
    @NotBlank(message = "A descrição do motivo (Formado, Transferido, etc) é obrigatória") String descricao
) {}
