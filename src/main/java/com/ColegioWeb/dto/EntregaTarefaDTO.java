package com.ColegioWeb.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EntregaTarefaDTO(
        Long entregaId,
        Long tarefaId,
        String tituloTarefa,
        String descricaoTarefa,
        String tipoTarefa,
        String disciplinaNome,
        LocalDate dataLimite,
        Long alunoId,
        String alunoNome,
        String status,
        LocalDateTime dataEntrega,
        Double nota
) {}
