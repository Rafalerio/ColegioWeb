package com.ColegioWeb.services;

import com.ColegioWeb.dto.MatriculaRequestDTO;
import com.ColegioWeb.models.Aluno;
import com.ColegioWeb.models.Responsavel;
import com.ColegioWeb.repositories.AlunoRepository;
import com.ColegioWeb.repositories.ResponsavelRepository;
import com.ColegioWeb.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MatriculaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ResponsavelRepository responsavelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void realizarMatricula(MatriculaRequestDTO dto) {
        
        // Validações de duplicidade
        if (usuarioRepository.existsByCpf(dto.cpfAluno())) {
            throw new IllegalArgumentException("Já existe um usuário com o CPF do aluno informado.");
        }
        if (usuarioRepository.existsByRg(dto.rgAluno())) {
            throw new IllegalArgumentException("Já existe um usuário com o RG do aluno informado.");
        }

        // Verifica se o responsável já existe pelo CPF
        Responsavel responsavel = responsavelRepository.findByCpf(dto.cpfResponsavel())
                .orElse(null);

        if (responsavel == null) {
            // Se o CPF do responsável já existir em outro tipo de usuário, lança erro
            if (usuarioRepository.existsByCpf(dto.cpfResponsavel())) {
                throw new IllegalArgumentException("O CPF do responsável já está cadastrado em outro perfil.");
            }
            if (usuarioRepository.existsByEmail(dto.emailResponsavel())) {
                throw new IllegalArgumentException("O e-mail do responsável já está em uso.");
            }

            responsavel = new Responsavel();
            responsavel.setNome(dto.nomeResponsavel());
            responsavel.setCpf(dto.cpfResponsavel());
            responsavel.setRg(dto.rgResponsavel());
            responsavel.setTelefone(dto.telefoneResponsavel());
            responsavel.setEmail(dto.emailResponsavel());
            responsavel.setEndereco(dto.endereco());
            responsavel.setSenha(passwordEncoder.encode(dto.senhaResponsavel()));
            responsavelRepository.save(responsavel);
        } else {
            // Em tese, atualizaria os dados do responsável se fizesse sentido
            // Mas vamos usar o já existente
        }

        // Criar Aluno
        Aluno aluno = new Aluno();
        aluno.setNome(dto.nomeAluno());
        aluno.setCpf(dto.cpfAluno());
        aluno.setRg(dto.rgAluno());
        aluno.setDataNascimento(dto.dataNascimentoAluno());
        
        // Formatar e-mail escolar: cpf@aprendizagem.edu.br
        String emailEscolar = dto.cpfAluno() + "@aprendizagem.edu.br";
        aluno.setEmail(emailEscolar);
        
        // Senha padrão é o CPF do aluno
        aluno.setSenha(passwordEncoder.encode(dto.cpfAluno()));
        
        // Gerar uma matricula unica (Ano + 4 digitos aleatorios)
        String ano = String.valueOf(java.time.Year.now().getValue());
        String aleatorio = String.format("%04d", new java.util.Random().nextInt(10000));
        aluno.setMatricula(ano + aleatorio);
        
        aluno.setResponsavel(responsavel);
        
        // Valores default já definidos na entidade (0)
        aluno.setQuantidadeDeFaltas(0);
        aluno.setMediaDeDesempenho(0.0);
        
        alunoRepository.save(aluno);
    }
}
