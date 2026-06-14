package com.ColegioWeb.controllers;

import com.ColegioWeb.dto.AvisoDTO;
import com.ColegioWeb.models.Aviso;
import com.ColegioWeb.models.Usuario;
import com.ColegioWeb.repositories.AvisoRepository;
import com.ColegioWeb.repositories.UsuarioRepository;
import com.ColegioWeb.services.AvisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/avisos")
@CrossOrigin(origins = "*")
public class AvisoController {

    @Autowired
    private AvisoService avisoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<Aviso> postarAviso(Principal principal, @RequestBody AvisoDTO dto) {
        Usuario user = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        return ResponseEntity.ok(avisoService.postarAviso(user.getId(), dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<String> editarAviso(@PathVariable Long id, Principal principal, @RequestBody AvisoDTO dto) {
        Usuario user = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        try {
            avisoService.editarAviso(id, user.getId(), dto);
            return ResponseEntity.ok("Aviso editado com sucesso.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<String> deletarAviso(@PathVariable Long id, Principal principal) {
        Usuario user = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        try {
            avisoService.deletarAviso(id, user.getId());
            return ResponseEntity.ok("Aviso deletado com sucesso.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
