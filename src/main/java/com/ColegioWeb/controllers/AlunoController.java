package com.ColegioWeb.controllers;

import com.ColegioWeb.services.AlunoService;
import com.ColegioWeb.models.Aluno;
import com.ColegioWeb.repositories.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aluno")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ALUNO')")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private AlunoRepository alunoRepository;

    private Long getAlunoId(Authentication authentication) {
        return alunoRepository.findByCpf(authentication.getName())
                .map(Aluno::getId)
                .orElseThrow(() -> new IllegalStateException("Aluno não autenticado."));
    }

    @PostMapping("/entregas/{entregaId}/entregar")
    public ResponseEntity<String> marcarComoEntregue(@PathVariable Long entregaId, Authentication authentication) {
        alunoService.marcarComoEntregue(getAlunoId(authentication), entregaId);
        return ResponseEntity.ok("Entrega realizada com sucesso.");
    }
}
