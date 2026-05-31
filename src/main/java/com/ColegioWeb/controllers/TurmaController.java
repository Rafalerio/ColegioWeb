package com.ColegioWeb.controllers;

import com.ColegioWeb.dto.TransferenciaTurmaDTO;
import com.ColegioWeb.dto.TurmaDTO;
import com.ColegioWeb.services.TurmaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*")
public class TurmaController {

    @Autowired
    private TurmaService turmaService;

    @GetMapping
    public ResponseEntity<List<TurmaDTO>> listarTodas() {
        return ResponseEntity.ok(turmaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TurmaDTO> criar(@Valid @RequestBody TurmaDTO dto) {
        return ResponseEntity.ok(turmaService.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TurmaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TurmaDTO dto) {
        return ResponseEntity.ok(turmaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Idealmente apenas ADMIN com nivelAcesso TOTAL
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        turmaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transferir")
    // Pode ser chamado por ADMIN, ou requerente ALUNO/RESPONSAVEL (simplificado)
    public ResponseEntity<String> transferirAluno(@Valid @RequestBody TransferenciaTurmaDTO dto) {
        try {
            turmaService.transferirAluno(dto);
            return ResponseEntity.ok("Aluno transferido com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
