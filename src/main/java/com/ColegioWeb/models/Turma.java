package com.ColegioWeb.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Entity
@NoArgsConstructor
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome; // Ex: Turma A

    @Column(nullable = false)
    private String serie; // Ex: 1º Ano, 2º Ano (Série)

    @Column(nullable = false)
    private String turno; // Ex: Matutino, Vespertino, Noturno (Turno)

    @Column(nullable = false)
    private Integer anoLetivo; // Ex: 2026 (Ano letivo)

    @OneToMany(mappedBy = "turma")
    private List<Aluno> alunos;
}
