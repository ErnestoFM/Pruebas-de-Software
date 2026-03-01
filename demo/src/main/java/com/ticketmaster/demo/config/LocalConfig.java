package com.ticketmaster.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class LocalConfig {

    //Configuracion del idioma en el front
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        Locale localeMx = new Locale.Builder()
                .setLanguage("es")
                .setRegion("MX")
                .build();
        slr.setDefaultLocale(localeMx);
        return slr;
    }
}