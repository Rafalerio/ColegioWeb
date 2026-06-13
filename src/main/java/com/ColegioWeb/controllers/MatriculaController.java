package com.ColegioWeb.controllers;

import com.ColegioWeb.dto.MatriculaRequestDTO;
import com.ColegioWeb.services.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matricula")
@CrossOrigin(origins = "*")
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    @PostMapping
    public ResponseEntity<String> matricular(@Valid @RequestBody MatriculaRequestDTO dto) {
        try {
            Long id = matriculaService.realizarMatricula(dto);
            return ResponseEntity.ok("Solicitação criada com sucesso! ID: " + id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
