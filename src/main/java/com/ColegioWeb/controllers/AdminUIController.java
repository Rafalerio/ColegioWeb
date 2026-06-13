package com.ColegioWeb.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUIController {

    @GetMapping("/avisos")
    public String avisos() {
        return "admin/avisos-admin";
    }

    @GetMapping("/turmas")
    public String turmas(Model model) {
        return "admin/turmas-admin";
    }

    @GetMapping("/disciplinas")
    public String disciplinas(Model model) {
        return "admin/disciplinas-admin";
    }

    @GetMapping("/matriculas")
    public String matriculas(Model model) {
        return "admin/solicitacoes-admin";
    }
}
