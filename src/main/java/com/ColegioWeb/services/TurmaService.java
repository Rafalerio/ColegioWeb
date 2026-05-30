package com.ColegioWeb.services;

import com.ColegioWeb.dto.TransferenciaTurmaDTO;
import com.ColegioWeb.dto.TurmaDTO;
import com.ColegioWeb.models.Aluno;
import com.ColegioWeb.models.Turma;
import com.ColegioWeb.repositories.AlunoRepository;
import com.ColegioWeb.repositories.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    public List<TurmaDTO> listarTodas() {
        return turmaRepository.findAll().stream()
                .map(t -> new TurmaDTO(t.getId(), t.getNome(), t.getSerie(), t.getTurno(), t.getAnoLetivo(), t.getCapacidadeMax()))
                .collect(Collectors.toList());
    }

    public TurmaDTO buscarPorId(Long id) {
        Turma t = turmaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));
        return new TurmaDTO(t.getId(), t.getNome(), t.getSerie(), t.getTurno(), t.getAnoLetivo(), t.getCapacidadeMax());
    }

    @Transactional
    public TurmaDTO criar(TurmaDTO dto) {
        Turma turma = new Turma();
        turma.setNome(dto.nome());
        turma.setSerie(dto.serie());
        turma.setTurno(dto.turno());
        turma.setAnoLetivo(dto.anoLetivo());
        if (dto.capacidadeMax() != null) {
            turma.setCapacidadeMax(dto.capacidadeMax());
        }
        turma = turmaRepository.save(turma);
        return new TurmaDTO(turma.getId(), turma.getNome(), turma.getSerie(), turma.getTurno(), turma.getAnoLetivo(), turma.getCapacidadeMax());
    }

    @Transactional
    public TurmaDTO atualizar(Long id, TurmaDTO dto) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));
        turma.setNome(dto.nome());
        turma.setSerie(dto.serie());
        turma.setTurno(dto.turno());
        turma.setAnoLetivo(dto.anoLetivo());
        if (dto.capacidadeMax() != null) {
            turma.setCapacidadeMax(dto.capacidadeMax());
        }
        turma = turmaRepository.save(turma);
        return new TurmaDTO(turma.getId(), turma.getNome(), turma.getSerie(), turma.getTurno(), turma.getAnoLetivo(), turma.getCapacidadeMax());
    }

    @Transactional
    public void deletar(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));
        if (turma.getAlunos() != null && !turma.getAlunos().isEmpty()) {
            throw new IllegalStateException("Não é possível deletar uma turma que possui alunos.");
        }
        turmaRepository.deleteById(id);
    }

    @Transactional
    public void transferirAluno(TransferenciaTurmaDTO dto) {
        Aluno aluno = alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));

        Turma novaTurma = turmaRepository.findById(dto.novaTurmaId())
                .orElseThrow(() -> new IllegalArgumentException("Nova turma não encontrada."));

        if (!aluno.isAtivo()) {
            throw new IllegalStateException("O aluno precisa estar ativo para ser transferido.");
        }

        if (aluno.getTurma() != null && aluno.getTurma().getId().equals(novaTurma.getId())) {
            throw new IllegalStateException("O aluno já está nesta turma.");
        }

        int totalAlunos = novaTurma.getAlunos() == null ? 0 : novaTurma.getAlunos().size();

        if (totalAlunos >= novaTurma.getCapacidadeMax()) {
            throw new IllegalStateException("A turma " + novaTurma.getNome() + " já atingiu a capacidade máxima de " + novaTurma.getCapacidadeMax() + " alunos.");
        }

        aluno.setTurma(novaTurma);
        alunoRepository.save(aluno);
    }
}
