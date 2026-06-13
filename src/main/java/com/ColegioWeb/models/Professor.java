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

    public Professor() {
        super();
        this.setTipoUsuario("PROFESSOR");
    }
}