package com.ColegioWeb.services;

import com.ColegioWeb.dto.EntregaTarefaDTO;
import com.ColegioWeb.models.Aluno;
import com.ColegioWeb.models.Avaliacao;
import com.ColegioWeb.models.EntregaTarefa;
import com.ColegioWeb.repositories.AlunoRepository;
import com.ColegioWeb.repositories.AvaliacaoRepository;
import com.ColegioWeb.repositories.EntregaTarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private EntregaTarefaRepository entregaTarefaRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Transactional(readOnly = true)
    public List<EntregaTarefaDTO> listarEntregasDoAluno(Long alunoId) {
        return entregaTarefaRepository.findByAlunoIdOrderByTarefaDataEntregaDesc(alunoId).stream().map(e -> new EntregaTarefaDTO(
                e.getId(), e.getTarefa().getId(), e.getTarefa().getTitulo(), e.getTarefa().getDescricao(), e.getTarefa().getTipo(),
                e.getTarefa().getDisciplina().getNome(), e.getTarefa().getDataEntrega(),
                e.getAluno().getId(), e.getAluno().getNome(), e.getStatus(), e.getDataEntrega(),
                e.getAvaliacao() != null ? e.getAvaliacao().getNota() : null
        )).collect(Collectors.toList());
    }

    @Transactional
    public void marcarComoEntregue(Long alunoId, Long entregaId) {
        EntregaTarefa entrega = entregaTarefaRepository.findById(entregaId).orElseThrow(() -> new IllegalArgumentException("Entrega não encontrada"));
        if (!entrega.getAluno().getId().equals(alunoId)) {
            throw new IllegalStateException("Esta entrega pertence a outro aluno.");
        }
        
        if ("PENDENTE".equals(entrega.getStatus())) {
            entrega.setStatus("ENTREGUE");
            entrega.setDataEntrega(LocalDateTime.now());
            entregaTarefaRepository.save(entrega);
        }
    }
    
    @Transactional(readOnly = true)
    public Double calcularMediaDisciplina(Long alunoId, Long disciplinaId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByAlunoId(alunoId).stream()
                .filter(a -> a.getDisciplina().getId().equals(disciplinaId))
                .collect(Collectors.toList());
        
        if (avaliacoes.isEmpty()) return null;
        
        double sum = avaliacoes.stream().mapToDouble(Avaliacao::getNota).sum();
        return sum / avaliacoes.size();
    }
}
