package com.ColegioWeb.security;

import com.ColegioWeb.models.Usuario;
import com.ColegioWeb.models.Aluno;
import com.ColegioWeb.models.Responsavel;
import com.ColegioWeb.models.SolicitacaoMatricula;
import com.ColegioWeb.models.StatusMatricula;
import com.ColegioWeb.repositories.UsuarioRepository;
import com.ColegioWeb.repositories.SolicitacaoMatriculaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SolicitacaoMatriculaRepository solicitacaoRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("DEBUG [Login]: Spring Security iniciou tentativa de login. Buscando email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("DEBUG [Login]: Falha! E-mail ({}) não encontrado no banco.", email);
                    return new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email);
                });

        log.info("DEBUG [Login]: Usuário encontrado no banco: {}. Validando senha...", usuario.getNome());

        boolean enabled = true;

        if (usuario instanceof Aluno) {
            Aluno aluno = (Aluno) usuario;
            List<SolicitacaoMatricula> solicitacoes = solicitacaoRepository.findByAlunoId(aluno.getId());
            boolean isConfirmada = solicitacoes.stream().anyMatch(s -> s.getStatus() == StatusMatricula.CONFIRMADA);
            if (!solicitacoes.isEmpty() && !isConfirmada) {
                enabled = false;
            }
        } else if (usuario instanceof Responsavel) {
            Responsavel responsavel = (Responsavel) usuario;
            if (responsavel.getAlunos() != null && !responsavel.getAlunos().isEmpty()) {
                boolean anyConfirmada = false;
                for (Aluno a : responsavel.getAlunos()) {
                    List<SolicitacaoMatricula> solicitacoes = solicitacaoRepository.findByAlunoId(a.getId());
                    if (solicitacoes.stream().anyMatch(s -> s.getStatus() == StatusMatricula.CONFIRMADA)) {
                        anyConfirmada = true;
                        break;
                    }
                }
                if (!anyConfirmada) {
                    enabled = false;
                }
            }
        }

        return new UserDetailsImpl(usuario, enabled);
    }
}
