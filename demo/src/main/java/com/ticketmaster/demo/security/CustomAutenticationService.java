package com.ticketmaster.demo.security;

import com.ticketmaster.demo.model.Usuario.User;
import com.ticketmaster.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomAutenticationService implements UserDetailsService {

    private final UserRepository usuarioRepository;

    /**
     * Carga un usuario por su nombre de usuario o correo electrónico.
     *
     * @param usernameOrEmail el nombre de usuario o correo electrónico del usuario a cargar
     * @return UserDetails que representa al usuario cargado
     * @throws UsernameNotFoundException si no se encuentra un usuario con el nombre de usuario o correo electrónico proporcionado
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User usuario = usuarioRepository.findByUserNameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail));

        return new CustomUserDetails(usuario);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User usuario) {
        return List.of(new SimpleGrantedAuthority(usuario.getRol().name()));
    }

}
