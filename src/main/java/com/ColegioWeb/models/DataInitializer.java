package com.ColegioWeb.models;

import com.ColegioWeb.models.Administrador;
import com.ColegioWeb.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existe um admin (para não criar duplicado a cada reinicialização)
        if (usuarioRepository.findByEmail("admin@colegioweb.com").isEmpty()) {
            Administrador admin = new Administrador();
            admin.setNome("Administrador Master");
            admin.setEmail("admin@colegioweb.com");
            // A senha será automaticamente encriptada pelo UsuarioService,
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setTipoUsuario("ADMIN");
            admin.setNivelAcesso("TOTAL");
            admin.setCargo("Root");

            usuarioRepository.save(admin);
            System.out.println("Usuário administrador mestre criado com sucesso! (admin@colegioweb.com / admin123)");
        }
    }
}