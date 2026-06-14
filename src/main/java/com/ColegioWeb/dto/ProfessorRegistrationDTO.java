package com.ColegioWeb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfessorRegistrationDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "O RG é obrigatório")
    private String rg;

    private String dataNascimento;
    private String telefone;
    private String endereco;


    @NotEmpty(message = "Selecione ao menos uma disciplina")
    private List<Long> disciplinasIds;
}
