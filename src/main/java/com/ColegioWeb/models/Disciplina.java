package com.ColegioWeb.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer cargaHoraria; // Ex de "carga horária"

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToMany(mappedBy = "disciplinas")
    private java.util.List<Professor> professores;

    @Column(nullable = false)
    private boolean ativo = true;
}
