package com.ColegioWeb.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Aluno extends Usuario {

    @Column(unique = true)
    private String matricula;

    @ManyToOne
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Responsavel responsavel;

    @Column(name = "quantidade_de_faltas")
    private Integer quantidadeDeFaltas = 0;

    @Column(name = "media_de_desempenho")
    private Double mediaDeDesempenho = 0.0;

    public Aluno() {
        super();
        this.setTipoUsuario("ALUNO");
    }
}

