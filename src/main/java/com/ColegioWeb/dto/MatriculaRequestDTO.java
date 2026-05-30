package com.ColegioWeb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record MatriculaRequestDTO(
        // Dados do Aluno
        @NotBlank(message = "O nome do aluno é obrigatório")
        String nomeAluno,

        @NotBlank(message = "O CPF do aluno é obrigatório")
        @Size(min = 11, max = 11, message = "O CPF do aluno deve ter 11 dígitos")
        String cpfAluno,

        @NotBlank(message = "O RG do aluno é obrigatório")
        String rgAluno,

        @NotBlank(message = "A data de nascimento é obrigatória")
        String dataNascimentoAluno,

        // Dados do Responsável
        @NotBlank(message = "O nome do responsável é obrigatório")
        String nomeResponsavel,

        @NotBlank(message = "O CPF do responsável é obrigatório")
        @Size(min = 11, max = 11, message = "O CPF do responsável deve ter 11 dígitos")
        String cpfResponsavel,

        @NotBlank(message = "O RG do responsável é obrigatório")
        @Size(min = 10, max = 10, message = "O RG do responsável deve ter 10 dígitos")
        String rgResponsavel,

        @NotBlank(message = "O telefone do responsável é obrigatório")
        @Pattern(regexp = "^\\(\\d{2}\\)9\\d{4}-\\d{4}$", message = "O telefone deve estar no formato (xx)9xxxx-xxxx")
        String telefoneResponsavel,

        @NotBlank(message = "O e-mail do responsável é obrigatório")
        @Email(message = "O e-mail do responsável deve ser válido")
        String emailResponsavel,

        @NotBlank(message = "O endereço é obrigatório")
        String endereco,

        @NotBlank(message = "A senha do responsável é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        @Pattern(regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$", message = "A senha deve conter pelo menos um caractere especial")
        String senhaResponsavel
) {
}
