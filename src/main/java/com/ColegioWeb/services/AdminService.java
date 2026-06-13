package com.ColegioWeb.services;

import com.ColegioWeb.models.Administrador;
import com.ColegioWeb.models.Usuario;
import com.ColegioWeb.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private com.ColegioWeb.repositories.HistoricoAlunoRepository historicoAlunoRepository;

    @Transactional
    public void alterarStatusUsuario(Long adminId, Long usuarioTargetId, boolean status) {
        // Busca o admin que está fazendo a ação (em um sistema real pegariamos via context do spring security)
        // Aqui simplificando:
        Usuario adminUser = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado."));

        if (!(adminUser instanceof Administrador)) {
            throw new IllegalStateException("Apenas administradores podem alterar o status de usuários.");
        }

        Administrador admin = (Administrador) adminUser;
        String nivel = admin.getNivelAcesso() != null ? admin.getNivelAcesso().toUpperCase() : "";

        if (nivel.equals("SUPORTE")) {
            throw new IllegalStateException("O nível SUPORTE não tem permissão para desativar/ativar usuários.");
        }

        Usuario target = usuarioRepository.findById(usuarioTargetId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário alvo não encontrado."));

        target.setAtivo(status);
        usuarioRepository.save(target);
    }

    @Transactional
    public void registrarHistoricoEDesativarAluno(Long adminId, Long alunoId, String descricao) {
        Usuario adminUser = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado."));

        if (!(adminUser instanceof Administrador)) {
            throw new IllegalStateException("Apenas administradores podem registrar histórico.");
        }

        Administrador admin = (Administrador) adminUser;
        String nivel = admin.getNivelAcesso() != null ? admin.getNivelAcesso().toUpperCase() : "";

        if (nivel.equals("SUPORTE")) {
            throw new IllegalStateException("O nível SUPORTE não tem permissão para arquivar alunos.");
        }

        Usuario target = usuarioRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));

        if (!(target instanceof com.ColegioWeb.models.Aluno)) {
            throw new IllegalArgumentException("O usuário selecionado não é um aluno.");
        }

        com.ColegioWeb.models.Aluno aluno = (com.ColegioWeb.models.Aluno) target;

        // Criar histórico escolar
        com.ColegioWeb.models.HistoricoAluno historico = new com.ColegioWeb.models.HistoricoAluno();
        historico.setAluno(aluno);
        historico.setDescricao(descricao);
        historico.setDataRegistro(java.time.LocalDate.now());

        // Registrar as informações de médias e faltas do período ativo
        String infoAcademica = String.format("Turma: %s | Faltas: %d | Média: %.2f",
                (aluno.getTurma() != null ? aluno.getTurma().getNome() : "Sem turma"),
                aluno.getQuantidadeDeFaltas(),
                aluno.getMediaDeDesempenho() != null ? aluno.getMediaDeDesempenho() : 0.0);

        historico.setInformacoesAcademicas(infoAcademica);
        historicoAlunoRepository.save(historico);

        // Desativar e limpar vínculo com a turma atual (pois não ocupa mais vaga)
        aluno.setAtivo(false);
        aluno.setTurma(null);
        usuarioRepository.save(aluno);
    }
}
