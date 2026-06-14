package com.ColegioWeb.controllers;

import com.ColegioWeb.dto.*;
import com.ColegioWeb.models.Professor;
import com.ColegioWeb.repositories.ProfessorRepository;
import com.ColegioWeb.services.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professor")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('PROFESSOR')")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ProfessorRepository professorRepository;

    private Long getProfessorId(Authentication authentication) {
        // login por cpf
        return professorRepository.findByCpf(authentication.getName())
                .map(Professor::getId)
                .orElseThrow(() -> new IllegalStateException("Professor não autenticado."));
    }

    @PostMapping("/faltas")
    public ResponseEntity<String> lancarFalta(@Valid @RequestBody FaltaDTO dto, Authentication authentication) {
        professorService.lancarFalta(getProfessorId(authentication), dto);
        return ResponseEntity.ok("Falta lançada com sucesso.");
    }

    @PostMapping("/avaliacoes")
    public ResponseEntity<String> lancarNota(@Valid @RequestBody AvaliacaoDTO dto, Authentication authentication) {
        professorService.lancarNota(getProfessorId(authentication), dto);
        return ResponseEntity.ok("Nota lançada com sucesso e média recalculada.");
    }


    @PostMapping("/tarefas")
    public ResponseEntity<String> criarTarefa(@Valid @RequestBody TarefaDTO dto, Authentication authentication) {
        professorService.criarTarefa(getProfessorId(authentication), dto);
        return ResponseEntity.ok("Tarefa criada com sucesso.");
    }

    @GetMapping("/situacao-aluno")
    public String situacaoAluno() {
        return "professor/situacao-aluno";
    }
}
