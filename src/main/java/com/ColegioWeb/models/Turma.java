package com.ColegioWeb.models;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
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

    @Column(nullable = false)
    private Integer capacidadeMax = 30; // Capacidade máxima de alunos

    @Column(nullable = false)
    private boolean ativo = true;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "turma")
    private List<Aluno> alunos;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "turma_disciplina",
        joinColumns = @JoinColumn(name = "turma_id"),
        inverseJoinColumns = @JoinColumn(name = "disciplina_id")
    )
    private List<Disciplina> disciplinas;
}
