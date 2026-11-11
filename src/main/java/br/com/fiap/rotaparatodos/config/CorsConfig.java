package br.com.fiap.rotaparatodos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                // PATCH e usado pelo gestor para mudar o status do relato
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
    }
}
