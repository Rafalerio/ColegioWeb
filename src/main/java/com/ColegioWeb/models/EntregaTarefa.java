package com.ColegioWeb.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EntregaTarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    private LocalDateTime dataEntrega;

    @Column(nullable = false)
    private String status; // PENDENTE, ENTREGUE, AVALIADO

    @OneToOne
    @JoinColumn(name = "avaliacao_id")
    private Avaliacao avaliacao;
}
