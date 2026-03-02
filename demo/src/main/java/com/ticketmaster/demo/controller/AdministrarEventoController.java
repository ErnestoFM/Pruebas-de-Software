package com.ticketmaster.demo.controller;

import com.ticketmaster.demo.dto.EventRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/events")
public class AdministrarEventoController {

    @Value("${app.image-api.base-url}")
    private String imageApiUrl;

    @GetMapping("/create")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new EventRequest());
        model.addAttribute("imageApiUrl", imageApiUrl);
        return "eventos/create";
    }

    @PostMapping("/create")
    public String createEvent(
            @Valid @ModelAttribute("event") EventRequest eventRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "events/create";
            // Events event = eventMapper.toEntity(eventRequest);
            // eventRepository.save(event);

            redirectAttributes.addFlashAttribute("successMessage", "Evento creado con éxito");
            return "redirect:/events";
        }

    }

}