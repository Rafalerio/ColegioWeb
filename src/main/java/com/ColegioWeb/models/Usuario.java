package com.ColegioWeb.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter @Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String rg;

    private String dataNascimento;

    private String telefone;

    private String endereco;

    @Column(nullable = false)
    private String tipoUsuario; // EX: ADMIN, PROFESSOR, ALUNO, RESPONSAVEL

    @Column(nullable = false)
    private boolean ativo = true; // Status do usuário (Ativo/Inativo)
}
