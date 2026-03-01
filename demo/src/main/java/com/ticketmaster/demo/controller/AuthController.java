package com.ticketmaster.demo.controller;

import com.ticketmaster.demo.dto.DtoRegistroUsuario;
import com.ticketmaster.demo.model.Usuario.User;
import com.ticketmaster.demo.service.RegistroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AuthController {


    private final RegistroService registroService;

    // =====================================
    // FORMULARIOS WEB TRADICIONALES
    // =====================================
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        // Asegúrate que este DTO tenga setters y getters
        model.addAttribute("registroUsuario", new DtoRegistroUsuario(
                null, null, null, null, null, null, null, null, null, null, null, null, false
        ));
        return "sesion/signUp";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("registroUsuario") @Valid DtoRegistroUsuario registroUsuario,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        // 1. Validaciones cruzadas manuales (Passwords iguales, emails iguales)
        validarDTO(registroUsuario, result);

        // 2. Si hay errores (ya sea de los @Pattern del DTO o de nuestro validarDTO)
        if (result.hasErrors()) {
            return "sesion/signUp"; // Volvemos al formulario para mostrar los errores en rojo
        }

        try {
            // 3. Intentamos guardar (Aquí adentro se genera el userName/Súper-CURP)
            User usuario = registroService.crearUsuarioBasico(registroUsuario);

            // 4. ÉXITO: Redirigimos al login con mensaje
            String successfulMessage = "Registro exitoso. Tu usuario es: " + usuario.getUsername() + ". Ahora puedes iniciar sesión.";
            redirectAttributes.addFlashAttribute("successfulRegistroUsuario", successfulMessage);
            return "redirect:/login";

        } catch (Exception e) {
            // 5. ERROR DE BASE DE DATOS (Ej. Correo duplicado)
            model.addAttribute("errorGlobal", "Error al registrar: " + e.getMessage());
            return "sesion/signUp";
        }
    }

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               Model model,
                               Principal principal) {
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Sesión cerrada correctamente");
        }
        if (principal != null) {
            return "redirect:/";
        }

        return "sesion/login";
    }

        // =====================================
        // VALIDACIONES PERSONALIZADAS
        // =====================================
        private void validarDTO(DtoRegistroUsuario registroUsuario, BindingResult result) {
            // 1. Validar coincidencia de EMAILS
            if (registroUsuario.email() != null && !registroUsuario.email().equals(registroUsuario.confirmEmail())) {
                result.rejectValue("confirmEmail", "error.confirmEmail", "Los correos electrónicos no coinciden");
            }

            // 2. Validar coincidencia de CONTRASEÑAS
            if (registroUsuario.password() != null && !registroUsuario.password().equals(registroUsuario.confirmPassword())) {
                result.rejectValue("confirmPassword", "error.confirmPassword", "Las contraseñas no coinciden");
            }
        }
    }

