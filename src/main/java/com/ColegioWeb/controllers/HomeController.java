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

    @Autowired
    private com.ColegioWeb.repositories.HistoricoMatriculaRepository historicoRepository;

    @Autowired
    private com.ColegioWeb.repositories.AvisoRepository avisoRepository;

    @Autowired
    private com.ColegioWeb.repositories.TurmaDisciplinaProfessorRepository tdpRepository;

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
    public String alunoAvisos(Model model, java.security.Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario instanceof com.ColegioWeb.models.Aluno) {
            com.ColegioWeb.models.Aluno aluno = (com.ColegioWeb.models.Aluno) usuario;
            if (aluno.getTurma() != null) {
                model.addAttribute("avisos", avisoRepository.findByTurmaIdOrIsGeralTrueOrderByDataCriacaoDesc(aluno.getTurma().getId()));
            } else {
                model.addAttribute("avisos", avisoRepository.findByIsGeralTrueOrderByDataCriacaoDesc());
            }
        } else if (usuario instanceof com.ColegioWeb.models.Responsavel) {
            com.ColegioWeb.models.Responsavel resp = (com.ColegioWeb.models.Responsavel) usuario;
            java.util.Set<com.ColegioWeb.models.Aviso> avisosSet = new java.util.HashSet<>(avisoRepository.findByIsGeralTrueOrderByDataCriacaoDesc());
            if (resp.getAlunos() != null) {
                for (com.ColegioWeb.models.Aluno a : resp.getAlunos()) {
                    if (a.getTurma() != null) {
                        avisosSet.addAll(avisoRepository.findByTurmaIdOrIsGeralTrueOrderByDataCriacaoDesc(a.getTurma().getId()));
                    }
                }
            }
            java.util.List<com.ColegioWeb.models.Aviso> avisosList = new java.util.ArrayList<>(avisosSet);
            avisosList.sort((a1, a2) -> a2.getDataCriacao().compareTo(a1.getDataCriacao()));
            model.addAttribute("avisos", avisosList);
        }
        return "aluno/avisos"; 
    }

    @GetMapping("/aluno/recusado")
    public String alunoRecusado() { return "aluno/recusado"; }

    @PostMapping("/aluno/reenviar-matricula")
    public String reenviarMatricula(java.security.Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario != null) {
            if (usuario instanceof com.ColegioWeb.models.Aluno) {
                java.util.List<SolicitacaoMatricula> solicitacoes = solicitacaoRepository.findByAlunoId(usuario.getId());
                for (SolicitacaoMatricula s : solicitacoes) {
                    if (s.getStatus() == com.ColegioWeb.models.StatusMatricula.RECUSADA || s.getStatus() == com.ColegioWeb.models.StatusMatricula.CANCELADA) {
                        com.ColegioWeb.models.StatusMatricula statusAnterior = s.getStatus();
                        s.setStatus(com.ColegioWeb.models.StatusMatricula.EM_ANALISE);
                        
                        com.ColegioWeb.models.HistoricoMatricula historico = new com.ColegioWeb.models.HistoricoMatricula();
                        historico.setSolicitacao(s);
                        historico.setStatusAnterior(statusAnterior);
                        historico.setStatusNovo(com.ColegioWeb.models.StatusMatricula.EM_ANALISE);
                        historico.setDataAlteracao(java.time.LocalDateTime.now());
                        historico.setAlteradoPor(usuario.getNome());
                        historicoRepository.save(historico);
                        
                        solicitacaoRepository.save(s);
                    }
                }
            } else if (usuario instanceof com.ColegioWeb.models.Responsavel) {
                com.ColegioWeb.models.Responsavel responsavel = (com.ColegioWeb.models.Responsavel) usuario;
                if (responsavel.getAlunos() != null) {
                    for (com.ColegioWeb.models.Aluno a : responsavel.getAlunos()) {
                        java.util.List<SolicitacaoMatricula> solicitacoes = solicitacaoRepository.findByAlunoId(a.getId());
                        for (SolicitacaoMatricula s : solicitacoes) {
                            if (s.getStatus() == com.ColegioWeb.models.StatusMatricula.RECUSADA || s.getStatus() == com.ColegioWeb.models.StatusMatricula.CANCELADA) {
                                com.ColegioWeb.models.StatusMatricula statusAnterior = s.getStatus();
                                s.setStatus(com.ColegioWeb.models.StatusMatricula.EM_ANALISE);
                                
                                com.ColegioWeb.models.HistoricoMatricula historico = new com.ColegioWeb.models.HistoricoMatricula();
                                historico.setSolicitacao(s);
                                historico.setStatusAnterior(statusAnterior);
                                historico.setStatusNovo(com.ColegioWeb.models.StatusMatricula.EM_ANALISE);
                                historico.setDataAlteracao(java.time.LocalDateTime.now());
                                historico.setAlteradoPor(usuario.getNome());
                                historicoRepository.save(historico);
                                
                                solicitacaoRepository.save(s);
                            }
                        }
                    }
                }
            }
        }
        return "redirect:/login?resubmitted=true";
    }

    @GetMapping("/aluno/calendario")
    public String alunoCalendario() { return "aluno/calendario"; }

    @Autowired
    private com.ColegioWeb.services.AlunoService alunoService;

    @GetMapping("/aluno/avaliacoes")
    public String alunoAvaliacoes(Model model, org.springframework.security.core.Authentication authentication) {
        Usuario usuario = usuarioRepository.findByCpf(authentication.getName()).orElse(null);
        if (usuario != null && "ALUNO".equals(usuario.getTipoUsuario())) {
            model.addAttribute("entregas", alunoService.listarEntregasDoAluno(usuario.getId()));
        }
        return "aluno/avaliacoes";
    }

    @GetMapping("/aluno/disciplinas")
    public String alunoDisciplinas() { return "aluno/disciplinas"; }

    @GetMapping("/aluno/sistema-faltas")
    public String alunoSistemaFaltas() { return "aluno/sistema-faltas"; }

    @GetMapping("/login-funcionario")
    public String loginFuncionario() { return "login-funcionario"; }

    @GetMapping("/professor/avisos")
    public String professorAvisos(Model model, java.security.Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario != null) {
            model.addAttribute("meusAvisos", avisoRepository.findByAutorIdOrderByDataCriacaoDesc(usuario.getId()));
            model.addAttribute("avisosGerais", avisoRepository.findByIsGeralTrueOrderByDataCriacaoDesc());
            java.util.List<com.ColegioWeb.models.TurmaDisciplinaProfessor> tdps = tdpRepository.findByProfessorId(usuario.getId());
            java.util.Set<com.ColegioWeb.models.Turma> turmasSet = new java.util.HashSet<>();
            for (var tdp : tdps) {
                turmasSet.add(tdp.getTurma());
            }
            model.addAttribute("turmas", turmasSet);
        }
        return "professor/avisos-professor"; 
    }

    @GetMapping("/professor/calendario")
    public String professorCalendario() { return "professor/calendario"; }

    @Autowired
    private com.ColegioWeb.services.ProfessorService professorService;

    @GetMapping("/professor/avaliacoes")
    public String professorAvaliacoes(Model model, org.springframework.security.core.Authentication authentication) {
        Usuario usuario = usuarioRepository.findByCpf(authentication.getName()).orElse(null);
        if (usuario != null && "PROFESSOR".equalsIgnoreCase(usuario.getTipoUsuario())) {
            model.addAttribute("turmasEDisciplinas", professorService.getTurmasEDisciplinas(usuario.getId()));
            model.addAttribute("tarefas", professorService.listarTarefas(usuario.getId()));
        }
        return "professor/avaliacoes";
    }

    @GetMapping("/professor/disciplinas")
    public String professorDisciplinas() { return "professor/disciplinas"; }

    @GetMapping("/professor/sistema-faltas")
    public String professorSistemaFaltas(Model model, org.springframework.security.core.Authentication authentication) {
        Usuario usuario = usuarioRepository.findByCpf(authentication.getName()).orElse(null);
        if (usuario != null && "PROFESSOR".equalsIgnoreCase(usuario.getTipoUsuario())) {
            model.addAttribute("turmasEDisciplinas", professorService.getTurmasEDisciplinas(usuario.getId()));
            model.addAttribute("resumoFaltas", professorService.getResumoFaltas(usuario.getId()));
        }
        return "professor/sistema-faltas";
    }

    @GetMapping("/professor/situacao-aluno")
    public String professorSituacaoAluno() { return "professor/situacao-aluno"; }
}