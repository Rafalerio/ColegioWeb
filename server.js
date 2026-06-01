require('dotenv').config();
const express = require('express');
const cors = require('cors');
const { GoogleGenerativeAI } = require('@google/generative-ai');

const app = express();
app.use(cors());
app.use(express.json());

// Inicializa o SDK do Gemini com a sua chave
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

// Define o prompt de sistema com as diretrizes do colégio
const systemPrompt = `Você é o assistente virtual do Colégio Aprendizagem. 
Seu objetivo é ajudar alunos, pais e docentes com dúvidas sobre o colégio.
Responda de forma educada, clara e concisa. 
Contexto atual da sessão:
Nome: [nome_usuario]
Página de origem: [pagina_origem]`;

app.post('/api/chat', async (req, res) => {
    try {
        const { mensagem } = req.body;

        // Atualizado para a versão 2.5-flash, a versão atual e suportada pelo Google
        const model = genAI.getGenerativeModel({
            model: "gemini-2.5-flash",
            systemInstruction: systemPrompt
        });

        const result = await model.generateContent(mensagem);
        const resposta = result.response.text();

        res.json({ resposta });

    } catch (error) {
        console.error("Erro ao comunicar com o Gemini:", error);
        res.status(500).json({ erro: "Desculpe, estou com problemas técnicos no momento." });
    }
});

// A parte abaixo é obrigatória para o servidor rodar
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
});