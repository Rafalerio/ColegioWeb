package com.ColegioWeb.config;

import com.ColegioWeb.models.Administrador;
import com.ColegioWeb.models.Professor;
import com.ColegioWeb.repositories.AdministradorRepository;
import com.ColegioWeb.repositories.ProfessorRepository;
import com.ColegioWeb.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
/*
@Configuration
public class DataInitializer{
    //ALERTA IMPORTANTE: Essa classe só deve ser inicializada com a API somente a primeira vez que rodar para injetar as informações no BD
    //Após a primeira inicialização, comente todo o código para não haver erros de inicialização por parte do BD não aceitar as informações duplicadas, ou retire o arquivo da pasta do projeto.
    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, ProfessorRepository professorRepository, AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByCpf("12345678901").isEmpty()) {
                Professor p1 = new Professor();
                p1.setNome("Professor Ardido");
                p1.setCpf("12345678901");
                p1.setEmail("ardido@aprendizagem.edu.br");
                p1.setSenha(passwordEncoder.encode("prof123"));
                p1.setEspecialidade("Matemática");
                p1.setTipoUsuario("PROFESSOR");
                professorRepository.save(p1);
            }

            if (usuarioRepository.findByCpf("00000000001").isEmpty()) {
                Administrador admin1 = new Administrador();
                admin1.setNome("Admin Master");
                admin1.setCpf("00000000001");
                admin1.setEmail("admin@aprendizagem.edu.br");
                admin1.setSenha(passwordEncoder.encode("admin123"));
                admin1.setTipoUsuario("ADMIN");
                admin1.setNivelAcesso("TOTAL");
                administradorRepository.save(admin1);
            }

            //Usuário para IA
            if (usuarioRepository.findByCpf("00000000002").isEmpty()) {
                Administrador admin2 = new Administrador();
                admin2.setNome("IA");
                admin2.setCpf("00000000002");
                admin2.setEmail("IA@aprendizagem.edu.br");
                admin2.setSenha(passwordEncoder.encode("admin.IA123"));
                admin2.setTipoUsuario("ADMIN");
                admin2.setNivelAcesso("SUPORTE");
                admin2.setCargo("IA");
                administradorRepository.save(admin2);
            }
        };
    }
}*/