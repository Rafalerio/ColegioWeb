package com.ColegioWeb.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Administrador extends Usuario {

    @Column
    private String cargo; // Ex: Diretor, Supervisor, Assistente Acadêmico

    @Column
    private String nivelAcesso; // Ex: TOTAL, RESTRITO, SUPORTE

    public Administrador() {
        super();
        this.setTipoUsuario("ADMIN");
    }
}