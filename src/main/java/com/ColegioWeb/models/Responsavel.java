package com.ColegioWeb.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Responsavel extends Usuario {

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "responsavel")
    private List<Aluno> alunos;

    public Responsavel() {
        super();
        this.setTipoUsuario("RESPONSAVEL");
    }
}
