package com.ColegioWeb.services;

import com.ColegioWeb.dto.AvisoDTO;
import com.ColegioWeb.models.Aviso;
import com.ColegioWeb.models.Turma;
import com.ColegioWeb.models.TurmaDisciplinaProfessor;
import com.ColegioWeb.models.Usuario;
import com.ColegioWeb.repositories.AvisoRepository;
import com.ColegioWeb.repositories.TurmaDisciplinaProfessorRepository;
import com.ColegioWeb.repositories.TurmaRepository;
import com.ColegioWeb.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvisoService {

    @Autowired
    private AvisoRepository avisoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private TurmaDisciplinaProfessorRepository tdpRepository;

    @Transactional
    public Aviso postarAviso(Long autorId, AvisoDTO dto) {
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado"));

        Aviso aviso = new Aviso();
        aviso.setAutor(autor);
        aviso.setTitulo(dto.titulo());
        aviso.setConteudo(dto.conteudo());
        aviso.setDataPublicacao(LocalDate.now());
        aviso.setDataCriacao(LocalDateTime.now());

        if (autor.getTipoUsuario().equals("ADMIN")) {
            if (dto.turmaId() == null) {
                aviso.setGeral(true);
            } else {
                Turma turma = turmaRepository.findById(dto.turmaId())
                        .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
                aviso.setTurma(turma);
                aviso.setGeral(false);
            }
        } else if (autor.getTipoUsuario().equals("PROFESSOR")) {
            if (dto.turmaId() == null) {
                throw new IllegalStateException("Professores não podem postar avisos gerais.");
            }
            Turma turma = turmaRepository.findById(dto.turmaId())
                    .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));

            List<TurmaDisciplinaProfessor> tdps = tdpRepository.findByTurmaId(turma.getId());
            boolean ensinaNestaTurma = tdps.stream().anyMatch(tdp -> tdp.getProfessor().getId().equals(autorId));
            if (!ensinaNestaTurma) {
                throw new IllegalStateException("O professor não leciona nesta turma.");
            }
            aviso.setTurma(turma);
            aviso.setGeral(false);
        } else {
            throw new IllegalStateException("Usuário não autorizado a postar avisos.");
        }

        return avisoRepository.save(aviso);
    }

    @Transactional
    public void editarAviso(Long avisoId, Long userId, AvisoDTO dto) {
        Aviso aviso = avisoRepository.findById(avisoId)
                .orElseThrow(() -> new IllegalArgumentException("Aviso não encontrado"));
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!user.getTipoUsuario().equals("ADMIN")) {
            if (!aviso.getAutor().getId().equals(userId)) {
                throw new IllegalStateException("Você só pode editar seus próprios avisos.");
            }
        }

        aviso.setTitulo(dto.titulo());
        aviso.setConteudo(dto.conteudo());
        
        if (user.getTipoUsuario().equals("ADMIN")) {
             if (dto.turmaId() == null) {
                aviso.setGeral(true);
                aviso.setTurma(null);
             } else {
                Turma turma = turmaRepository.findById(dto.turmaId())
                        .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
                aviso.setTurma(turma);
                aviso.setGeral(false);
             }
        }
        avisoRepository.save(aviso);
    }

    @Transactional
    public void deletarAviso(Long avisoId, Long userId) {
        Aviso aviso = avisoRepository.findById(avisoId)
                .orElseThrow(() -> new IllegalArgumentException("Aviso não encontrado"));
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!user.getTipoUsuario().equals("ADMIN")) {
            if (!aviso.getAutor().getId().equals(userId)) {
                throw new IllegalStateException("Você só pode deletar seus próprios avisos.");
            }
        }

        avisoRepository.delete(aviso);
    }
}
