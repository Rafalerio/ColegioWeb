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

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @Column(nullable = false)
    private boolean ativo = true;
}
