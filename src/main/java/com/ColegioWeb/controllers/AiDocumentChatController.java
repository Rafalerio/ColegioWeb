package com.ColegioWeb.controllers;

import com.ColegioWeb.dto.ChatResponseDTO;
import com.ColegioWeb.services.AiDocumentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class AiDocumentChatController {

    @Autowired
    private AiDocumentService aiDocumentService;

    @PostMapping("/analisar")
    public ResponseEntity<ChatResponseDTO> analisarDocumento(
            @RequestParam(value = "documento", required = false) MultipartFile documento,
            @RequestParam("mensagem") String mensagem,
            HttpSession session) {

        try {
            // Recupera ou inicializa a história de chat da sessão do professor
            List<Map<String, Object>> chatHistory = (List<Map<String, Object>>) session.getAttribute("chatHistory");
            if (chatHistory == null) {
                chatHistory = new java.util.ArrayList<>();
                session.setAttribute("chatHistory", chatHistory);
            }

            // Repassa o arquivo, a mensagem e a lista de história para o serviço de IA
            String respostaIa = aiDocumentService.analisarDocumentoComIa(documento, mensagem, chatHistory);

            return ResponseEntity.ok(new ChatResponseDTO(respostaIa, "sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ChatResponseDTO("Erro ao processar o documento: " + e.getMessage(), "erro"));
        }
    }

    @PostMapping("/limpar")
    public ResponseEntity<Map<String, String>> limparHistorico(HttpSession session) {
        session.removeAttribute("chatHistory");
        return ResponseEntity.ok(Map.of("status", "sucesso", "resposta", "Histórico de chat limpo com sucesso."));
    }
}