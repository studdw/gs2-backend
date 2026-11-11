package br.com.fiap.rotaparatodos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Cria o objeto que gera e confere o hash BCrypt das senhas (RNF01).
// Usa so a biblioteca de criptografia do Spring Security, nao o Spring Security completo.
@Configuration
public class SenhaConfig {

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

}
