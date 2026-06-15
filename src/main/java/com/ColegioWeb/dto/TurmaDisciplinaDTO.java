package com.ColegioWeb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurmaDisciplinaDTO {
    private Long turmaId;
    private String turmaNome;
    private Long disciplinaId;
    private String disciplinaNome;
}
