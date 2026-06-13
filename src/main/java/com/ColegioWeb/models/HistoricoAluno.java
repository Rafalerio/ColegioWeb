package com.ColegioWeb.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HistoricoAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false)
    private String descricao; // Ex: Transferência, Formatura, Trancamento

    @Column(nullable = false)
    private LocalDate dataRegistro;

    @Column(length = 2000)
    private String informacoesAcademicas; // JSON ou texto com médias e turmas passadas
}
