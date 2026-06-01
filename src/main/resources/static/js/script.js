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

// Função atualizada para enviar e receber mensagens da IA
async function enviarMensagem() {
    const texto = input.value.trim();

    if (texto === "") return;

    // 1. Exibe a mensagem do usuário
    mensagens.innerHTML += `
        <div class="user-message">
            ${texto}
        </div>
    `;
    input.value = "";
    mensagens.scrollTop = mensagens.scrollHeight;

    // 2. Adiciona um indicador de "digitando..."
    const idCarregando = "msg-" + Date.now();
    mensagens.innerHTML += `
        <div class="bot-message" id="${idCarregando}">
            <em>Digitando...</em>
        </div>
    `;
    mensagens.scrollTop = mensagens.scrollHeight;

    // 3. Faz a requisição para o Back-end
    try {
        const response = await fetch("http://localhost:3000/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ mensagem: texto })
        });

        const data = await response.json();

        // 4. Substitui a mensagem de "digitando..." pela resposta do Gemini
        const mensagemBot = document.getElementById(idCarregando);

        if (data.resposta) {
            // Convertendo quebras de linha para <br> no HTML
            mensagemBot.innerHTML = data.resposta.replace(/\n/g, '<br>');
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

// Eventos de clique e teclado (Mantidos do seu código original)
sendButton.onclick = enviarMensagem;

input.addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
        enviarMensagem();
    }
});