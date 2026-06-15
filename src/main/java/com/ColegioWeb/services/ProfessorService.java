package com.ColegioWeb.services;

import com.ColegioWeb.dto.AvaliacaoDTO;
import com.ColegioWeb.dto.AvisoDTO;
import com.ColegioWeb.dto.FaltaDTO;
import com.ColegioWeb.dto.TarefaDTO;
import com.ColegioWeb.models.*;
import com.ColegioWeb.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private FaltaRepository faltaRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private TurmaDisciplinaProfessorRepository turmaDisciplinaProfessorRepository;

    @Autowired
    private EntregaTarefaRepository entregaTarefaRepository;

    @Transactional
    public void lancarFalta(Long professorId, FaltaDTO dto) {
        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        Aluno aluno = alunoRepository.findById(dto.alunoId()).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));
        Disciplina disciplina = disciplinaRepository.findById(dto.disciplinaId()).orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));

        TurmaDisciplinaProfessor tdp = turmaDisciplinaProfessorRepository.findByTurmaIdAndDisciplinaId(aluno.getTurma().getId(), disciplina.getId());
        if (tdp == null || !tdp.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalStateException("O professor não leciona esta disciplina para a turma do aluno.");
        }

        Falta falta = new Falta();
        falta.setAluno(aluno);
        falta.setDisciplina(disciplina);
        falta.setDataFalta(dto.dataFalta());
        faltaRepository.save(falta);

        aluno.setQuantidadeDeFaltas(aluno.getQuantidadeDeFaltas() + 1);
        alunoRepository.save(aluno);
    }

    @Transactional
    public void lancarNota(Long professorId, AvaliacaoDTO dto) {
        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        Aluno aluno = alunoRepository.findById(dto.alunoId()).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));
        Disciplina disciplina = disciplinaRepository.findById(dto.disciplinaId()).orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));

        TurmaDisciplinaProfessor tdp = turmaDisciplinaProfessorRepository.findByTurmaIdAndDisciplinaId(aluno.getTurma().getId(), disciplina.getId());
        if (tdp == null || !tdp.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalStateException("O professor não leciona esta disciplina para a turma do aluno.");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAluno(aluno);
        avaliacao.setDisciplina(disciplina);
        avaliacao.setNota(dto.nota());
        avaliacao.setObservacao(dto.observacao());
        avaliacao.setDataAvaliacao(dto.dataAvaliacao());
        avaliacaoRepository.save(avaliacao);

        recalcularMediaAluno(aluno);
    }

    private void recalcularMediaAluno(Aluno aluno) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByAlunoId(aluno.getId());
        if (avaliacoes.isEmpty()) {
            aluno.setMediaDeDesempenho(0.0);
        } else {
            double soma = avaliacoes.stream().mapToDouble(Avaliacao::getNota).sum();
            aluno.setMediaDeDesempenho(soma / avaliacoes.size());
        }
        alunoRepository.save(aluno);
    }

    @Transactional(readOnly = true)
    public List<Tarefa> listarTarefas(Long professorId) {
        return tarefaRepository.findByProfessorIdOrderByDataEntregaDesc(professorId);
    }

    @Transactional
    public void criarTarefa(Long professorId, TarefaDTO dto) {
        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        Turma turma = turmaRepository.findById(dto.turmaId()).orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));
        Disciplina disciplina = disciplinaRepository.findById(dto.disciplinaId()).orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));

        java.util.List<TurmaDisciplinaProfessor> tdps = turmaDisciplinaProfessorRepository.findByTurmaIdAndDisciplinaId(turma.getId(), disciplina.getId()) == null ? 
            java.util.Collections.emptyList() : java.util.List.of(turmaDisciplinaProfessorRepository.findByTurmaIdAndDisciplinaId(turma.getId(), disciplina.getId()));
        
        boolean ensinaNestaTurmaEDisciplina = tdps.stream().anyMatch(tdp -> tdp != null && tdp.getProfessor().getId().equals(professor.getId()));
        if (!ensinaNestaTurmaEDisciplina) {
            throw new IllegalStateException("O professor não leciona esta disciplina nesta turma.");
        }

        Tarefa tarefa = new Tarefa();
        tarefa.setProfessor(professor);
        tarefa.setTurma(turma);
        tarefa.setDisciplina(disciplina);
        tarefa.setTipo(dto.tipo());
        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setDataEntrega(dto.dataEntrega());
        tarefa = tarefaRepository.save(tarefa);

        for (Aluno aluno : turma.getAlunos()) {
            EntregaTarefa entrega = new EntregaTarefa();
            entrega.setTarefa(tarefa);
            entrega.setAluno(aluno);
            entrega.setStatus("PENDENTE");
            entregaTarefaRepository.save(entrega);
        }
    }
    @Transactional(readOnly = true)
    public List<com.ColegioWeb.dto.EntregaTarefaDTO> listarEntregasPorTarefa(Long professorId, Long tarefaId) {
        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        Tarefa tarefa = tarefaRepository.findById(tarefaId).orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));
        
        if (!tarefa.getProfessor().getId().equals(professorId)) {
            throw new IllegalStateException("Esta tarefa pertence a outro professor.");
        }

        return entregaTarefaRepository.findByTarefaId(tarefaId).stream().map(e -> new com.ColegioWeb.dto.EntregaTarefaDTO(
                e.getId(), e.getTarefa().getId(), e.getTarefa().getTitulo(), e.getTarefa().getDescricao(), e.getTarefa().getTipo(),
                e.getTarefa().getDisciplina().getNome(), e.getTarefa().getDataEntrega(),
                e.getAluno().getId(), e.getAluno().getNome(), e.getStatus(), e.getDataEntrega(),
                e.getAvaliacao() != null ? e.getAvaliacao().getNota() : null
        )).collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void avaliarEntrega(Long professorId, Long entregaId, Double nota, String observacao) {
        EntregaTarefa entrega = entregaTarefaRepository.findById(entregaId).orElseThrow(() -> new IllegalArgumentException("Entrega não encontrada."));
        if (!entrega.getTarefa().getProfessor().getId().equals(professorId)) {
            throw new IllegalStateException("Esta tarefa pertence a outro professor.");
        }

        Avaliacao avaliacao = entrega.getAvaliacao();
        if (avaliacao == null) {
            avaliacao = new Avaliacao();
            avaliacao.setAluno(entrega.getAluno());
            avaliacao.setDisciplina(entrega.getTarefa().getDisciplina());
            avaliacao.setDataAvaliacao(java.time.LocalDate.now());
        }
        avaliacao.setNota(nota);
        avaliacao.setObservacao(observacao);
        avaliacao = avaliacaoRepository.save(avaliacao);

        entrega.setAvaliacao(avaliacao);
        entrega.setStatus("AVALIADO");
        entregaTarefaRepository.save(entrega);
    }

    @Transactional
    public void removerFalta(Long professorId, Long faltaId) {
        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        Falta falta = faltaRepository.findById(faltaId).orElseThrow(() -> new IllegalArgumentException("Falta não encontrada."));
        
        TurmaDisciplinaProfessor tdp = turmaDisciplinaProfessorRepository.findByTurmaIdAndDisciplinaId(falta.getAluno().getTurma().getId(), falta.getDisciplina().getId());
        if (tdp == null || !tdp.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalStateException("O professor não leciona esta disciplina para a turma do aluno.");
        }
        
        Aluno aluno = falta.getAluno();
        faltaRepository.delete(falta);
        
        if (aluno.getQuantidadeDeFaltas() > 0) {
            aluno.setQuantidadeDeFaltas(aluno.getQuantidadeDeFaltas() - 1);
            alunoRepository.save(aluno);
        }
    }

    @Transactional(readOnly = true)
    public List<com.ColegioWeb.dto.FaltaAgrupadaDTO> getResumoFaltas(Long professorId) {
        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        List<TurmaDisciplinaProfessor> tdps = turmaDisciplinaProfessorRepository.findByProfessorId(professorId);
        
        List<com.ColegioWeb.dto.FaltaAgrupadaDTO> resumo = new java.util.ArrayList<>();
        
        for (TurmaDisciplinaProfessor tdp : tdps) {
            Turma turma = tdp.getTurma();
            Disciplina disciplina = tdp.getDisciplina();
            
            for (Aluno aluno : turma.getAlunos()) {
                long count = faltaRepository.countByAlunoIdAndDisciplinaId(aluno.getId(), disciplina.getId());
                if (count > 0) {
                    resumo.add(new com.ColegioWeb.dto.FaltaAgrupadaDTO(
                        aluno.getId(), aluno.getNome(), 
                        disciplina.getId(), disciplina.getNome(), 
                        turma.getId(), turma.getNome(), 
                        count));
                }
            }
        }
        return resumo;
    }

    public List<com.ColegioWeb.dto.FaltaDetalheDTO> getDetalhesFalta(Long professorId, Long alunoId, Long disciplinaId) {
        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));
        
        TurmaDisciplinaProfessor tdp = turmaDisciplinaProfessorRepository.findByTurmaIdAndDisciplinaId(aluno.getTurma().getId(), disciplinaId);
        if (tdp == null || !tdp.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalStateException("O professor não leciona esta disciplina para a turma do aluno.");
        }
        
        return faltaRepository.findByAlunoIdAndDisciplinaIdOrderByDataFaltaDesc(alunoId, disciplinaId)
                .stream()
                .map(f -> new com.ColegioWeb.dto.FaltaDetalheDTO(f.getId(), f.getDataFalta()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.ColegioWeb.dto.TurmaDisciplinaDTO> getTurmasEDisciplinas(Long professorId) {
        return turmaDisciplinaProfessorRepository.findByProfessorId(professorId)
                .stream()
                .map(tdp -> new com.ColegioWeb.dto.TurmaDisciplinaDTO(tdp.getTurma().getId(), tdp.getTurma().getNome(), tdp.getDisciplina().getId(), tdp.getDisciplina().getNome()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.ColegioWeb.dto.AlunoSimplificadoDTO> getAlunosPorTurma(Long professorId, Long turmaId) {
        List<TurmaDisciplinaProfessor> tdps = turmaDisciplinaProfessorRepository.findByProfessorId(professorId);
        boolean teaches = tdps.stream().anyMatch(tdp -> tdp.getTurma().getId().equals(turmaId));
        if (!teaches) {
            throw new IllegalStateException("O professor não leciona nesta turma.");
        }
        
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));
        return turma.getAlunos()
                .stream()
                .map(a -> new com.ColegioWeb.dto.AlunoSimplificadoDTO(a.getId(), a.getNome()))
                .collect(java.util.stream.Collectors.toList());
    }
}
