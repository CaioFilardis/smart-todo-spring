package com.projetoprionyx.smart_todo.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesteController {

    @GetMapping("/teste") // indica mapea o método get
    public String testar() {
        return "Testando 123";
    }
}
