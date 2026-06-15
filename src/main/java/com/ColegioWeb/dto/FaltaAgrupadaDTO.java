package com.ColegioWeb.dto;

public record FaltaAgrupadaDTO(
        Long alunoId,
        String alunoNome,
        Long disciplinaId,
        String disciplinaNome,
        Long turmaId,
        String turmaNome,
        long quantidadeFaltas
) {}
