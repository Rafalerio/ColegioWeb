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
public class HistoricoMatricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private SolicitacaoMatricula solicitacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMatricula statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMatricula statusNovo;

    @Column(nullable = false)
    private LocalDateTime dataAlteracao;

    @Column(nullable = false)
    private String alteradoPor; // Nome ou CPF do admin/usuário
}
