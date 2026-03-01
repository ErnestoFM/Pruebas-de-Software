package com.ticketmaster.demo.controller;

import com.ticketmaster.demo.model.Estudiante;
import com.ticketmaster.demo.model.Events;
import com.ticketmaster.demo.model.Usuario.User;
import com.ticketmaster.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.EnumSet;

@Controller
public class InicioController {
    private final UserRepository userRepository;

    public InicioController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre("Ernesto Hatuey Fierro Melendez");
        estudiante.setCodigo("223977036");
        estudiante.setCarrera("Ingeniería en Sistemas");
        model.addAttribute("activePage", "home");
        model.addAttribute("estudiante", estudiante);
        return "index";
    }

    @GetMapping("/museo")
    public String museo(Model model) {

        model.addAttribute("activePage", "museo");
        return "museo/museos";
    }

    @GetMapping("/museo/compra")
    public String museoCompra(@RequestParam Events events, Model model) {
        model.addAttribute("museo", events);
        return "museo/compra";
    }

    @GetMapping("/teatro")
    public String teatro(Model model) {

        model.addAttribute("activePage", "teatro");
        return "teatro/teatros";
    }

    @GetMapping("/teatro/compra")
    public String teatroCompra(@RequestParam Events events, Model model) {
        Events evento = new Events(
                null,                                   // id
                "Blanca nieves",                        // nombre
                120,                                    // duracion
                true,                                   // activo
                LocalDate.now(),                        // fechaPublicacion
                LocalDate.now().plusDays(2),            // fechaEstreno
                EnumSet.of(Events.RopaPermitida.CASUAL),// ropaPermitida
                Events.TipoEvento.TEATRO,               // tipo
                100,                                    // stock
                5,                                      // maxBoletosPorUsuario
                true,                                   // abiertoEnFestivos
                EnumSet.of(Events.DiasDisponibles.LUNES, Events.DiasDisponibles.MARTES), // diasDisponibles
                null                                    // <--- AQUÍ ESTABA EL ERROR (Faltaba asientos)
        );
        model.addAttribute("obra", evento);
        model.addAttribute("teatro", events);
        return "teatro/compra";
    }

    @GetMapping("/cine")
    public String cine(Model model) {

        model.addAttribute("activePage", "cine");
        return "cine/cines";
    }

    @GetMapping("/perfil/mis-boletos")
    public String boletos(Authentication authentication, Model model) {
        String superCurp = authentication.getName();
        User usuarioLogueado = userRepository.findByUserName(superCurp).orElseThrow();

        // 2. Mandamos el usuario a la vista
        model.addAttribute("usuario", usuarioLogueado);
        model.addAttribute("activePage", "boletos");


        return "perfil/boletos";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Authentication authentication, Model model) {
        String superCurp = authentication.getName();
        User usuarioLogueado = userRepository.findByUserName(superCurp).orElseThrow();
        model.addAttribute("usuario", usuarioLogueado);

        return "perfil/perfil";
    }

    @GetMapping("/perfil/configuracion")
    public String mostrarConfiguracion(Authentication authentication, Model model) {
        if (authentication != null) {
            String superCurp = authentication.getName();

            // Buscamos al usuario igual que en el perfil
            User usuarioLogueado = userRepository.findByUserName(superCurp).orElseThrow();

            // Lo mandamos a la vista
            model.addAttribute("usuario", usuarioLogueado);
        }

        return "perfil/configuracion";
    }
}
