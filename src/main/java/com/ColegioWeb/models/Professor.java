package com.ColegioWeb.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Professor extends Usuario {

    @Column
    private String especialidade;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @jakarta.persistence.ManyToMany(fetch = jakarta.persistence.FetchType.EAGER)
    @jakarta.persistence.JoinTable(
        name = "professor_disciplina",
        joinColumns = @jakarta.persistence.JoinColumn(name = "professor_id"),
        inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "disciplina_id")
    )
    private java.util.List<Disciplina> disciplinas;

    public Professor() {
        super();
        this.setTipoUsuario("PROFESSOR");
    }
}