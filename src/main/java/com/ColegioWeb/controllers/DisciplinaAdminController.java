package com.ColegioWeb.controllers;

import com.ColegioWeb.models.Disciplina;
import com.ColegioWeb.repositories.DisciplinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/disciplinas")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class DisciplinaAdminController {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @GetMapping
    public ResponseEntity<List<Disciplina>> listarTodas() {
        return ResponseEntity.ok(disciplinaRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Disciplina> criar(@RequestBody Disciplina disciplina) {
        return ResponseEntity.ok(disciplinaRepository.save(disciplina));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> mudarStatus(@PathVariable Long id, @RequestParam boolean ativo) {
        Disciplina d = disciplinaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada"));
        d.setAtivo(ativo);
        disciplinaRepository.save(d);
        return ResponseEntity.ok("Status atualizado");
    }
}
