package com.ColegioWeb.dto;

import java.time.LocalDate;

public record FaltaDetalheDTO(
        Long faltaId,
        LocalDate dataFalta
) {}
