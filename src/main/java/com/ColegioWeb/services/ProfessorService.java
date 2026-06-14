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

    @Transactional
    public void criarTarefa(Long professorId, TarefaDTO dto) {
        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        Turma turma = turmaRepository.findById(dto.turmaId()).orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));

        java.util.List<TurmaDisciplinaProfessor> tdps = turmaDisciplinaProfessorRepository.findByTurmaId(turma.getId());
        boolean ensinaNestaTurma = tdps.stream().anyMatch(tdp -> tdp.getProfessor().getId().equals(professor.getId()));
        if (!ensinaNestaTurma) {
            throw new IllegalStateException("O professor não leciona nesta turma.");
        }

        Tarefa tarefa = new Tarefa();
        tarefa.setProfessor(professor);
        tarefa.setTurma(turma);
        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setDataEntrega(dto.dataEntrega());
        tarefaRepository.save(tarefa);
    }
}
