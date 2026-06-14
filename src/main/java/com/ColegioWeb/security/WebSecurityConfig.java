package com.ColegioWeb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/login-funcionario", "/recuperar-senha", "/api/auth/recuperar-senha", "/cadastro", "/comprovante", "/sobre", "/contato", "/segmentos", "/css/**", "/js/**", "/images/**", "/api/matricula").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/professor/**").hasRole("PROFESSOR")
                        .requestMatchers("/aluno/recusado", "/aluno/reenviar-matricula").hasRole("RECUSADO")
                        .requestMatchers("/aluno/**").hasAnyRole("ALUNO", "RESPONSAVEL")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")

                        .successHandler((request, response, authentication) -> {
                            var roles = authentication.getAuthorities().stream()
                                    .map(a -> a.getAuthority())
                                    .toList();

                            String referer = request.getHeader("Referer");
                            System.out.println("DEBUG [Login Sucesso]: Usuário autenticado com as roles: " + roles);
                            System.out.println("DEBUG [Login Origem]: Veio da página: " + referer);

                            // Se tentou logar pela página dos funcionarios
                            if (referer != null && referer.contains("login-funcionario")) {
                                if (roles.contains("ROLE_PROFESSOR")) {
                                    response.sendRedirect("/professor/avisos");
                                    return;
                                } else if (roles.contains("ROLE_ADMIN")) {
                                    response.sendRedirect("/admin/avisos");
                                    return;
                                } else {
                                    System.out.println("DEBUG [Login Bloqueado]: Professor/Admin não encontrado");
                                    request.getSession().invalidate();
                                    response.sendRedirect("/login-funcionario?error=not_funcionario");
                                    return;
                                }
                            }

                            // Fluxo padrão pela tela de login dos alunos
                            if (roles.contains("ROLE_PROFESSOR") || roles.contains("ROLE_ADMIN")) {
                                System.out.println("DEBUG [Login Bloqueado]: Professores e Administradores devem usar o Portal do Servidor");
                                request.getSession().invalidate();
                                response.sendRedirect("/login?error=not_student");
                                return;
                            } else if (roles.contains("ROLE_RECUSADO")) {
                                response.sendRedirect("/aluno/recusado");
                                return;
                            } else {
                                response.sendRedirect("/aluno/avisos");
                            }
                        })

                        // Redireciona a falha para a página correta de origem
                        .failureHandler((request, response, exception) -> {
                            String referer = request.getHeader("Referer");
                            System.out.println("DEBUG [Login Falha]: Credenciais incorretas ou acesso bloqueado. Erro: " + exception.getMessage());

                            if (referer != null && referer.contains("login-funcionario")) {
                                response.sendRedirect("/login-funcionario?error=true");
                            } else {
                                if (exception instanceof org.springframework.security.authentication.DisabledException) {
                                    response.sendRedirect("/login?error=pending");
                                } else {
                                    response.sendRedirect("/login?error=true");
                                }
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Encriptação de senha sólida (BCrypt) protegendo contra vazamentos
        return new BCryptPasswordEncoder();
    }
}
