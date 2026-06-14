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
import com.ColegioWeb.models.Disciplina;
import com.ColegioWeb.repositories.DisciplinaRepository;
import java.util.Arrays;
import java.util.List;

/*
@Configuration
public class DataInitializer{
    //ALERTA IMPORTANTE: Essa classe só deve ser inicializada com a API somente a primeira vez que rodar para injetar as informações no BD
    //Após a primeira inicialização, comente todo o código para não haver erros de inicialização por parte do BD não aceitar as informações duplicadas, ou retire o arquivo da pasta do projeto.
    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, ProfessorRepository professorRepository, AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder, DisciplinaRepository disciplinaRepository, org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        return args -> {

            try {
                jdbcTemplate.execute("ALTER TABLE aviso DROP COLUMN professor_id");
                System.out.println("Coluna 'professor_id' removida com sucesso da tabela 'aviso'.");
            } catch (Exception e) {
                System.out.println("A coluna 'professor_id' já foi removida ou não existe.");
            }

//
            try {
                jdbcTemplate.execute("ALTER TABLE aviso MODIFY turma_id BIGINT NULL");
                System.out.println("Coluna 'turma_id' modificada para aceitar NULL.");
            } catch (Exception e) {
                System.out.println("A coluna 'turma_id' já permite NULL ou ocorreu um erro.");
            }

            // Inicializar disciplinas fixas se não existirem
            List<String> disciplinasFixas = Arrays.asList(
                    "Lingua Portuguesa", "Matemática", "Historia", "Geografia", "Ciências",
                    "Lingua Inglesa", "Artes", "Educação Física", "Biologia", "Física",
                    "Química", "Sociologia", "Filosofia"
            );
            
            for (String nomeDisciplina : disciplinasFixas) {
                if (disciplinaRepository.findByNome(nomeDisciplina).isEmpty()) {
                    Disciplina d = new Disciplina();
                    d.setNome(nomeDisciplina);
                    d.setCargaHoraria(80); // Carga horária padrão
                    disciplinaRepository.save(d);
                }
            }

            if (usuarioRepository.findByCpf("12345678901").isEmpty()) {
                Professor p1 = new Professor();
                p1.setNome("Professor Ardido");
                p1.setCpf("12345678901");
                p1.setEmail("ardido@aprendizagem.edu.br");
                p1.setSenha(passwordEncoder.encode("prof123"));
                p1.setEspecialidade("Matemática");
                p1.setTipoUsuario("PROFESSOR");
                
                // Mapear a disciplina
                disciplinaRepository.findByNome("Matemática").ifPresent(d -> {
                    p1.setDisciplinas(Arrays.asList(d));
                });

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