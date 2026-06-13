package com.ColegioWeb.controllers;

import com.ColegioWeb.models.PeriodoMatricula;
import com.ColegioWeb.models.SolicitacaoMatricula;
import com.ColegioWeb.models.StatusMatricula;
import com.ColegioWeb.repositories.PeriodoMatriculaRepository;
import com.ColegioWeb.repositories.SolicitacaoMatriculaRepository;
import com.ColegioWeb.repositories.HistoricoMatriculaRepository;
import com.ColegioWeb.repositories.TurmaRepository;
import com.ColegioWeb.repositories.AlunoRepository;
import com.ColegioWeb.models.Turma;
import com.ColegioWeb.models.Aluno;
import com.ColegioWeb.models.HistoricoMatricula;
import java.time.LocalDateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/matriculas")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class MatriculaAdminController {

    @Autowired
    private PeriodoMatriculaRepository periodoRepository;

    @Autowired
    private SolicitacaoMatriculaRepository solicitacaoRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private HistoricoMatriculaRepository historicoRepository;

    @GetMapping("/periodos")
    public ResponseEntity<List<PeriodoMatricula>> listarPeriodos() {
        return ResponseEntity.ok(periodoRepository.findAll());
    }

    @PostMapping("/periodos")
    public ResponseEntity<PeriodoMatricula> criarPeriodo(@RequestBody PeriodoMatricula periodo) {
        return ResponseEntity.ok(periodoRepository.save(periodo));
    }

    @PutMapping("/periodos/{id}/status")
    public ResponseEntity<String> mudarStatusPeriodo(@PathVariable Long id, @RequestParam boolean aberto) {
        PeriodoMatricula p = periodoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Período não encontrado"));
        p.setAberto(aberto);
        periodoRepository.save(p);
        return ResponseEntity.ok("Status do período atualizado para " + (aberto ? "Aberto" : "Fechado"));
    }

    @GetMapping("/solicitacoes")
    public ResponseEntity<List<SolicitacaoMatricula>> listarSolicitacoes() {
        return ResponseEntity.ok(solicitacaoRepository.findAll());
    }

    @PutMapping("/solicitacoes/{id}/status")
    public ResponseEntity<String> julgarSolicitacao(@PathVariable Long id, @RequestParam StatusMatricula status, @RequestParam(required = false) Long turmaId) {
        SolicitacaoMatricula sol = solicitacaoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        
        StatusMatricula statusAnterior = sol.getStatus();
        
        // RN 04 e 05: Registra o histórico e o admin responsável
        String adminResponsavel = SecurityContextHolder.getContext().getAuthentication().getName();
        
        HistoricoMatricula historico = new HistoricoMatricula();
        historico.setSolicitacao(sol);
        historico.setStatusAnterior(statusAnterior);
        historico.setStatusNovo(status);
        historico.setDataAlteracao(LocalDateTime.now());
        historico.setAlteradoPor(adminResponsavel);
        
        historicoRepository.save(historico);

        sol.setStatus(status);
        
        if (status == StatusMatricula.CONFIRMADA && turmaId != null) {
            Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
            
            sol.setTurma(turma);
            
            Aluno aluno = sol.getAluno();
            aluno.setTurma(turma);
            alunoRepository.save(aluno);
        }
        
        solicitacaoRepository.save(sol);
        return ResponseEntity.ok("Solicitação " + status.name());
    }
}
