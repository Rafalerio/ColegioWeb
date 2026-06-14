package com.ColegioWeb.dto;

public class ChatResponseDTO {
    private String resposta;
    private String status;

    public ChatResponseDTO() {}

    public ChatResponseDTO(String resposta, String status) {
        this.resposta = resposta;
        this.status = status;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}