package br.com.fiap.rotaparatodos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Rota raiz: mostra que a API esta no ar (util para conferir o deploy no Render)
@RestController
public class StatusController {

    @GetMapping("/")
    public String status(){
        return "API Rota para Todos no ar!";
    }

}
