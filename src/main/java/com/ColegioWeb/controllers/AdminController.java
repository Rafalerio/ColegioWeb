package com.ColegioWeb.controllers;

import com.ColegioWeb.models.Usuario;
import com.ColegioWeb.repositories.UsuarioRepository;
import com.ColegioWeb.services.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private com.ColegioWeb.repositories.TurmaDisciplinaProfessorRepository tdpRepository;
    
    @Autowired
    private com.ColegioWeb.repositories.TurmaRepository turmaRepository;
    
    @Autowired
    private com.ColegioWeb.repositories.DisciplinaRepository disciplinaRepository;
    
    @Autowired
    private com.ColegioWeb.repositories.ProfessorRepository professorRepository;

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PutMapping("/usuarios/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> alterarStatus(@PathVariable Long id, @RequestParam boolean ativo, @RequestParam Long adminId) {
        // Idealmente adminId vem do token / SecurityContextHolder, passando via request param pra simplificar testes.
        try {
            adminService.alterarStatusUsuario(adminId, id, ativo);
            return ResponseEntity.ok("Status atualizado com sucesso para: " + (ativo ? "Ativo" : "Inativo"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/usuarios/alunos/{id}/arquivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> arquivarAluno(@PathVariable Long id, @Valid @org.springframework.web.bind.annotation.RequestBody com.ColegioWeb.dto.HistoricoDTO dto) {
        try {
            adminService.registrarHistoricoEDesativarAluno(dto.adminId(), id, dto.descricao());
            return ResponseEntity.ok("Aluno arquivado e histórico gerado com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/turmas/{turmaId}/atribuicao")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> atribuirProfessor(@PathVariable Long turmaId, @RequestParam Long disciplinaId, @RequestParam Long professorId) {
        com.ColegioWeb.models.Turma turma = turmaRepository.findById(turmaId).orElseThrow();
        com.ColegioWeb.models.Disciplina disciplina = disciplinaRepository.findById(disciplinaId).orElseThrow();
        com.ColegioWeb.models.Professor professor = professorRepository.findByIdWithDisciplinas(professorId).orElseThrow();
        
        if (professor.getDisciplinas().stream().noneMatch(d -> d.getId().equals(disciplinaId))) {
            return ResponseEntity.badRequest().body("O professor não está qualificado para lecionar esta disciplina.");
        }
        
        com.ColegioWeb.models.TurmaDisciplinaProfessor existente = tdpRepository.findByTurmaIdAndDisciplinaId(turmaId, disciplinaId);
        if (existente != null) {
            if (existente.getProfessor().getId().equals(professorId)) {
                return ResponseEntity.badRequest().body("Este professor já está atribuído a esta disciplina nesta turma.");
            } else {
                return ResponseEntity.badRequest().body("Já existe um professor atribuído a esta disciplina nesta turma. Remova-o primeiro.");
            }
        }
        
        com.ColegioWeb.models.TurmaDisciplinaProfessor tdp = new com.ColegioWeb.models.TurmaDisciplinaProfessor();
        tdp.setTurma(turma);
        tdp.setDisciplina(disciplina);
        tdp.setProfessor(professor);
        tdpRepository.save(tdp);
        
        return ResponseEntity.ok("Professor atribuído com sucesso.");
    }
    
    @DeleteMapping("/turmas/atribuicao/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removerAtribuicao(@PathVariable Long id) {
        tdpRepository.deleteById(id);
        return ResponseEntity.ok("Atribuição removida com sucesso.");
    }
    
    @GetMapping("/turmas/{turmaId}/atribuicoes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<com.ColegioWeb.models.TurmaDisciplinaProfessor>> listarAtribuicoes(@PathVariable Long turmaId) {
        return ResponseEntity.ok(tdpRepository.findByTurmaId(turmaId));
    }
}
