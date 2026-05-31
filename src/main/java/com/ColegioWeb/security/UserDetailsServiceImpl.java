package com.ColegioWeb.security;

import com.ColegioWeb.models.Usuario;
import com.ColegioWeb.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("DEBUG [Login]: Spring Security iniciou tentativa de login. Buscando email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("DEBUG [Login]: Falha! E-mail ({}) não encontrado no banco.", email);
                    return new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email);
                });

        log.info("DEBUG [Login]: Usuário encontrado no banco: {}. Validando senha...", usuario.getNome());
        return new UserDetailsImpl(usuario);
    }
}
