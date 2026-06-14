// Carrossel
const slides = document.getElementById("slides");

if (slides) {
    let atual = 0;
    const total = slides.children.length;

    document.getElementById("next").onclick = () => {
        atual++;
        if (atual >= total) {
            atual = 0;
        }
        atualizar();
    };

    document.getElementById("prev").onclick = () => {
        atual--;
        if (atual < 0) {
            atual = total - 1;
        }
        atualizar();
    };

    function atualizar() {
        slides.style.transform = `translateX(-${atual * 100}%)`;
    }

    setInterval(() => {
        atual++;
        if (atual >= total) {
            atual = 0;
        }
        atualizar();
    }, 5000);
}



// Chat
const chatButton = document.getElementById("chat-button");
const chatBox = document.getElementById("chat-box");
const input = document.getElementById("chat-input");
const sendButton = document.getElementById("send-button");
const mensagens = document.querySelector(".chat-messages");

// Toggle do chat
if (chatButton && chatBox) {
    chatButton.addEventListener("click", () => {
        if (chatBox.style.display === "flex") {
            chatBox.style.display = "none";
        } else {
            chatBox.style.display = "flex";
        }
    });
}

// Histórico do Chat em memória (stateless no backend)
let chatHistory = [];

// Função de mensagens IA
async function enviarMensagem() {
    const texto = input.value.trim();

    if (texto === "") return;

    // Exibe a mensagem do usuário
    mensagens.innerHTML += `
        <div class="user-message">
            ${texto}
        </div>
    `;
    input.value = "";
    mensagens.scrollTop = mensagens.scrollHeight;

    // Adiciona a mensagem atual ao histórico local
    chatHistory.push({ role: 'user', parts: [{ text: texto }] });

    // Adiciona um indicador de "digitando..."
    const idCarregando = "msg-" + Date.now();
    mensagens.innerHTML += `
        <div class="bot-message" id="${idCarregando}">
            <em>Digitando...</em>
        </div>
    `;
    mensagens.scrollTop = mensagens.scrollHeight;

    // Faz requisição para o Back-end
    try {
        const response = await fetch("http://localhost:3000/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ mensagem: texto, historico: chatHistory })
        });

        const data = await response.json();

        // Substitui a mensagem de "digitando..." pela resposta do Gemini
        const mensagemBot = document.getElementById(idCarregando);

        if (data.resposta) {
            // Converte quebras de linha para <br> no HTML
            mensagemBot.innerHTML = data.resposta.replace(/\n/g, '<br>');
            // Adiciona a resposta da IA ao histórico local
            chatHistory.push({ role: 'model', parts: [{ text: data.resposta }] });
        } else {
            mensagemBot.innerHTML = "Erro ao obter resposta.";
        }

    } catch (error) {
        console.error("Erro:", error);
        const mensagemBot = document.getElementById(idCarregando);
        mensagemBot.innerHTML = "Não foi possível conectar ao servidor.";
    }

    mensagens.scrollTop = mensagens.scrollHeight;
}

// Eventos de clique e teclado
sendButton.onclick = enviarMensagem;

input.addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
        enviarMensagem();
    }
});