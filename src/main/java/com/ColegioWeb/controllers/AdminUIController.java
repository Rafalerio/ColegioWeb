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

    @org.springframework.beans.factory.annotation.Autowired
    private com.ColegioWeb.repositories.AvisoRepository avisoRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private com.ColegioWeb.repositories.TurmaRepository turmaRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private com.ColegioWeb.repositories.DisciplinaRepository disciplinaRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private com.ColegioWeb.repositories.ProfessorRepository professorRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private com.ColegioWeb.services.AdminService adminService;

    @GetMapping("/cadastrar-professor")
    public String exibirCadastroProfessor(Model model) {
        model.addAttribute("professorDto", new com.ColegioWeb.dto.ProfessorRegistrationDTO());
        model.addAttribute("disciplinas", disciplinaRepository.findAll());
        return "admin/cadastrar-professor";
    }

    @org.springframework.web.bind.annotation.PostMapping("/cadastrar-professor")
    public String cadastrarProfessor(@jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("professorDto") com.ColegioWeb.dto.ProfessorRegistrationDTO dto,
                                     org.springframework.validation.BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("disciplinas", disciplinaRepository.findAll());
            return "admin/cadastrar-professor";
        }
        try {
            adminService.registrarProfessor(dto);
            return "redirect:/admin/turmas?sucesso"; 
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("disciplinas", disciplinaRepository.findAll());
            return "admin/cadastrar-professor";
        }
    }

    @GetMapping("/avisos")
    public String avisos(Model model) {
        model.addAttribute("avisos", avisoRepository.findAllByOrderByDataCriacaoDesc());
        model.addAttribute("turmas", turmaRepository.findAll());
        return "admin/avisos-admin";
    }

    @GetMapping("/atribuicao-professores")
    public String atribuicaoProfessores(Model model) {
        model.addAttribute("turmas", turmaRepository.findAll());
        model.addAttribute("disciplinas", disciplinaRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());
        return "admin/atribuicao-professores";
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
