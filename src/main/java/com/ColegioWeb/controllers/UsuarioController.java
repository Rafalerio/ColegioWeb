package com.ColegioWeb.controllers;

import com.ColegioWeb.services.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        // Pega a lista do banco atraves do Service e passa para o Thymeleaf
        model.addAttribute("listaUsuarios", service.listarTodos());
        // Aponta para um arquivo chamado usuarios.html dentro de src/main/resources/templates/
        return "usuarios";
    }
}