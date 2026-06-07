package com.ColegioWeb.services;

import com.ColegioWeb.models.Administrador;
import com.ColegioWeb.models.Usuario;
import com.ColegioWeb.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public void alterarStatusUsuario(Long adminId, Long usuarioTargetId, boolean status) {
        // Busca o admin que está fazendo a ação (em um sistema real pegariamos via context do spring security)
        Usuario adminUser = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado."));

        if (!(adminUser instanceof Administrador)) {
            throw new IllegalStateException("Apenas administradores podem alterar o status de usuários.");
        }

        Administrador admin = (Administrador) adminUser;
        String nivel = admin.getNivelAcesso() != null ? admin.getNivelAcesso().toUpperCase() : "";

        if (nivel.equals("SUPORTE")) {
            throw new IllegalStateException("O nível SUPORTE não tem permissão para desativar/ativar usuários.");
        }

        Usuario target = usuarioRepository.findById(usuarioTargetId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário alvo não encontrado."));

        target.setAtivo(status);
        usuarioRepository.save(target);
    }
}
