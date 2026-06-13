package com.ColegioWeb.controllers;

import com.ColegioWeb.dto.MatriculaRequestDTO;
import com.ColegioWeb.services.MatriculaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.ColegioWeb.dto.RecuperacaoSenhaDTO;
import com.ColegioWeb.models.Usuario;
import com.ColegioWeb.models.SolicitacaoMatricula;
import com.ColegioWeb.repositories.UsuarioRepository;
import com.ColegioWeb.repositories.PeriodoMatriculaRepository;
import com.ColegioWeb.repositories.SolicitacaoMatriculaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PeriodoMatriculaRepository periodoRepository;

    @Autowired
    private SolicitacaoMatriculaRepository solicitacaoRepository;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("periodos", periodoRepository.findByAbertoTrue());
        return "cadastro";
    }

    @GetMapping("/recuperar-senha")
    public String recuperarSenha(Model model) {
        model.addAttribute("dto", new RecuperacaoSenhaDTO("", "", ""));
        return "recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String realizarRecuperacaoSenha(@Valid @ModelAttribute("dto") RecuperacaoSenhaDTO dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "recuperar-senha";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(dto.email());

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("erro", "Usuário não encontrado com os dados fornecidos.");
            return "recuperar-senha";
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getCpf() == null || !usuario.getCpf().equals(dto.cpf())) {
            model.addAttribute("erro", "Os dados fornecidos não conferem.");
            return "recuperar-senha";
        }

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);

        return "redirect:/login?recuperado";
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/contato")
    public String contato() {
        return "contato";
    }

    @GetMapping("/segmentos")
    public String segmentos() {
        return "segmentos";
    }

    @PostMapping("/cadastro")
    public String realizarCadastro(MatriculaRequestDTO dto) {
        log.info("=========================================");
        log.info("DEBUG [Cadastro]: Tentando cadastrar matrícula:");
        log.info("Aluno: {}", dto.nomeAluno());
        log.info("Responsável: {}", dto.nomeResponsavel());
        log.info("Período ID: {}", dto.periodoId());
        log.info("=========================================");

        try {
            Long solicitacaoId = matriculaService.realizarMatricula(dto);
            log.info("DEBUG [Cadastro]: Sucesso! Solicitação criada no banco com ID {}.", solicitacaoId);
            return "redirect:/comprovante?id=" + solicitacaoId;
        } catch (Exception e) {
            log.error("DEBUG [Cadastro]: Erro ao salvar: {}", e.getMessage());
            return "redirect:/cadastro?erro=true";
        }
    }

    @GetMapping("/comprovante")
    public String comprovante(@RequestParam Long id, Model model) {
        SolicitacaoMatricula solicitacao = solicitacaoRepository.findById(id).orElse(null);
        if (solicitacao == null) {
            return "redirect:/";
        }
        model.addAttribute("solicitacao", solicitacao);
        return "comprovante";
    }

    @GetMapping("/aluno/avisos")
    public String alunoAvisos() { return "aluno/avisos"; }

    @GetMapping("/aluno/calendario")
    public String alunoCalendario() { return "aluno/calendario"; }

    @GetMapping("/aluno/avaliacoes")
    public String alunoAvaliacoes() { return "aluno/avaliacoes"; }

    @GetMapping("/aluno/disciplinas")
    public String alunoDisciplinas() { return "aluno/disciplinas"; }

    @GetMapping("/login-funcionario")
    public String loginFuncionario() { return "login-funcionario"; }

    @GetMapping("/professor/avisos")
    public String professorAvisos() { return "professor/avisos-professor"; }

    @GetMapping("/professor/calendario")
    public String professorCalendario() { return "professor/calendario"; }

    @GetMapping("/professor/avaliacoes")
    public String professorAvaliacoes() { return "professor/avaliacoes"; }

    @GetMapping("/professor/financeiro")
    public String professorFinanceiro() { return "professor/financeiro"; }

    @GetMapping("/professor/notificacoes")
    public String professorNotificacoes() { return "professor/notificacoes"; }

    @GetMapping("/professor/situacao-aluno")
    public String professorSituacaoAluno() { return "professor/situacao-aluno"; }

    @GetMapping("/professor/sistema-faltas")
    public String professorSistemaFaltas() { return "professor/sistema-faltas"; }
}