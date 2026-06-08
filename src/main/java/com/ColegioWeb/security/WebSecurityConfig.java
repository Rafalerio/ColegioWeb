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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/cadastro", "/login-professor", "/sobre", "/css/**", "/js/**", "/images/**", "/api/matricula").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/professor/**").hasRole("PROFESSOR")
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

                            // Se tentou logar pela página de professor
                            if (referer != null && referer.contains("login-professor")) {
                                if (!roles.contains("ROLE_PROFESSOR")) {
                                    System.out.println("DEBUG [Login Bloqueado]: Professor não encontrado");
                                    request.getSession().invalidate();
                                    response.sendRedirect("/login-professor?error=not_professor");
                                    return;
                                }
                                response.sendRedirect("/professor/avisos");
                                return;
                            }

                            // Fluxo padrão se logou pela tela comum (Aluno / Responsável)
                            if (roles.contains("ROLE_PROFESSOR")) {
                                response.sendRedirect("/professor/avisos");
                            } else {
                                response.sendRedirect("/aluno/avisos");
                            }
                        })

                        // Redireciona a falha para a página correta de origem
                        .failureHandler((request, response, exception) -> {
                            String referer = request.getHeader("Referer");
                            System.out.println("DEBUG [Login Falha]: Credenciais incorretas. Erro: " + exception.getMessage());

                            if (referer != null && referer.contains("login-professor")) {
                                response.sendRedirect("/login-professor?error=true");
                            } else {
                                response.sendRedirect("/login?error=true");
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
        // Encriptação de senha sólida (BCrypt) protegendo contra vazamentos (LGPD)
        return new BCryptPasswordEncoder();
    }
}
