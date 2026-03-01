package com.ticketmaster.demo.security;

import com.ticketmaster.demo.model.Usuario.User;
import com.ticketmaster.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public boolean comprobarPassword(String username, String rawPassword) {
        // Aquí obtienes tu entidad Usuario en lugar de UserDetails
        Optional<User> usuario = userRepository.findByUserNameOrEmail(username, username);
        // Compara la contraseña cruda con la encriptada en la base de datos
        return usuario.filter(value -> passwordEncoder.matches(rawPassword, value.getPassword())).isPresent();
    }
}