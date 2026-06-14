package com.ColegioWeb.services;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiDocumentService {

    @Value("${ia.api.key:}")
    private String apiKey;

    // URL padrão da API do Gemini (Google)
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public String analisarDocumentoComIa(MultipartFile documento, String mensagemProfessor, List<Map<String, Object>> chatHistory) throws Exception {
        String textoDocumento = "";

        // 1. Extração de Texto do Documento usando Apache Tika
        if (documento != null && !documento.isEmpty()) {
            Tika tika = new Tika();
            textoDocumento = tika.parseToString(documento.getInputStream());
            // Se foi enviado um novo documento, a gente limpa a história anterior
            chatHistory.clear();
        }

        // 2. Adiciona a mensagem atual à história
        if (chatHistory.isEmpty()) {
            // É a primeira mensagem do chat, então constrói o prompt inicial contendo o documento (se houver)
            String promptCompleto = construirPrompt(textoDocumento, mensagemProfessor);
            
            Map<String, Object> userPart = Map.of("text", promptCompleto);
            Map<String, Object> userMessage = Map.of(
                "role", "user",
                "parts", List.of(userPart)
            );
            chatHistory.add(userMessage);
        } else {
            // É uma mensagem de acompanhamento
            Map<String, Object> userPart = Map.of("text", mensagemProfessor);
            Map<String, Object> userMessage = Map.of(
                "role", "user",
                "parts", List.of(userPart)
            );
            chatHistory.add(userMessage);
        }

        // 3. Chamada à API da Inteligência Artificial passando o histórico completo
        String respostaIa = chamarApiIA(chatHistory);

        // 4. Salva a resposta da IA na história
        Map<String, Object> modelPart = Map.of("text", respostaIa);
        Map<String, Object> modelMessage = Map.of(
            "role", "model",
            "parts", List.of(modelPart)
        );
        chatHistory.add(modelMessage);

        return respostaIa;
    }

    private String construirPrompt(String textoDocumento, String mensagemProfessor) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Você é um assistente educacional que apoia professores na análise de risco de evasão escolar.\n");
        prompt.append("REGRA ESTILHAÇADORA: Você DEVE basear sua resposta EXCLUSIVAMENTE nas informações contidas no documento abaixo. ");
        prompt.append("Não invente, não deduza e não utilize dados externos para justificar fatos que deveriam estar no laudo do aluno.\n\n");

        if (textoDocumento != null && !textoDocumento.isEmpty()) {
            prompt.append("--- INÍCIO DO DOCUMENTO DO ALUNO ---\n");
            prompt.append(textoDocumento).append("\n");
            prompt.append("--- FIM DO DOCUMENTO DO ALUNO ---\n\n");
        } else {
            prompt.append("[Aviso: Nenhum documento de laudo/histórico foi enviado na primeira mensagem. Ajude o professor de forma geral ou com base em assuntos de ENEM e estudos se solicitado.]\n\n");
        }

        prompt.append("Pergunta/Comando do Professor: ").append(mensagemProfessor);

        return prompt.toString();
    }

    private String chamarApiIA(List<Map<String, Object>> contents) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Aviso do Sistema: A chave da API da IA não está configurada no application.properties.";
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Define a instrução de sistema que orienta o comportamento geral da IA
        Map<String, Object> systemInstructionPart = Map.of("text", 
            "Você é o Assistente Virtual Oficial do Colégio Aprendizagem. Seu objetivo é apoiar os professores na análise de risco de evasão escolar, no rendimento pedagógico e no direcionamento de estudos dos alunos. " +
            "Se o assunto for relacionado ao ENEM (Exame Nacional do Ensino Médio) ou disciplinas da escola, pesquise na internet se necessário e forneça sugestões úteis de estudo ou informações atualizadas baseadas nas diretrizes do ENEM. " +
            "Responda de forma clara, acolhedora e profissional. Quando for fornecido um documento do aluno, baseie sua análise prioritariamente nas informações dele."
        );
        Map<String, Object> systemInstruction = Map.of("parts", List.of(systemInstructionPart));

        // Ferramentas de busca do Google (Search Grounding)
        List<Map<String, Object>> tools = List.of(Map.of("googleSearch", Map.of()));

        // Estrutura de JSON exigida pela API do Gemini
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("contents", contents);
        requestBodyMap.put("systemInstruction", systemInstruction);
        requestBodyMap.put("tools", tools);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBodyMap, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_API_URL + apiKey, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            // Navegando no JSON de resposta do Gemini para extrair o texto
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> partsList = (List<Map<String, Object>>) content.get("parts");

            return (String) partsList.get(0).get("text");

        } catch (Exception e) {
            System.err.println("Erro ao chamar a API da IA: " + e.getMessage());
            return "Desculpe, ocorreu um erro ao comunicar com o servidor de Inteligência Artificial.";
        }
    }
}