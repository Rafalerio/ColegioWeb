package com.ColegioWeb.controllers;

import com.ColegioWeb.models.Usuario;
import com.ColegioWeb.repositories.UsuarioRepository;
import com.ColegioWeb.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UsuarioRepository usuarioRepository; // Temporário para listar usuários

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
}
