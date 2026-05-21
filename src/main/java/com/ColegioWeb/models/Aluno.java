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

    public Aluno() {
        super();
        this.setTipoUsuario("ALUNO");
    }
}

